package com.reubbishsecurity.CovidVaccinations.frontend.controllers;

import com.reubbishsecurity.CovidVaccinations.authentication.UserValidator;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.Role;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;
import com.reubbishsecurity.CovidVaccinations.authentication.exception.UserNotFoundException;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment.AppointmentType;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment.VaccinationCenter;
import com.reubbishsecurity.CovidVaccinations.frontend.messages.AppointmentAvailability;
import com.reubbishsecurity.CovidVaccinations.frontend.messages.CheckAvailabilityOnDate;
import com.reubbishsecurity.CovidVaccinations.frontend.repository.AppointmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class FrontendController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AppointmentsRepository appointmentsRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private static final String[] nationalities = Arrays.stream(Nationality.values()).map(Enum::name).toArray(String[]::new);
    private static final String[] genders = Arrays.stream(User.Gender.values()).map(Enum::name).toArray(String[]::new);
    private static final UserValidator validator = new UserValidator();

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        User user = userRepository.findByPps(principal.getName()).get();

        System.out.println(user);
        if (user.mfa_enabled && !user.mfa_confirmed) {
            return "redirect:/mfa/enable";
        }

        List<Appointment> appts = appointmentsRepository.findByUser(user).stream().filter(appointment -> appointment.getComplete() == false).collect(Collectors.toList());
        model.addAttribute("name", user.getName());
        model.addAttribute("appointments", appts);
        model.addAttribute("hasFirstDose", user.getFirst_dose() != null);
        model.addAttribute("hasSecondDose", user.getSecond_dose() != null);
        if(user.getFirst_dose() != null) {
            model.addAttribute("first_dose", user.getFirst_dose().toString());
        }
        if(user.getSecond_dose() != null) {
            model.addAttribute("second_dose", user.getSecond_dose().toString());
        }
        model.addAttribute("lastActivity", formatLastActivity(user.getLastactivity()));
        return "index.html";
    }

    @GetMapping("/login")
    public String login(){
        return "login.html";
    }

    @PostMapping("/processLogin")
    public String process_login() {
        return "redirect:/";
    }

    @GetMapping("/mfa/confirm")
    public String confirm_mfa(Principal principal) {
        User user = userRepository.findByPps(principal.getName()).get();
        user.mfa_confirmed = true;
        userRepository.save(user);
        SecurityContextHolder.getContext().setAuthentication(null); // Log out user
        return "redirect:/";
    }

    @GetMapping("/mfa/enable")
    public String enable_mfa(Principal principal, Model model) throws UnsupportedEncodingException {
        User user = userRepository.findByPps(principal.getName()).get();
        model.addAttribute("qr", generateQRUrl(user));
        return "confirm-mfa.html";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("nationalities", nationalities);
        model.addAttribute("genders", genders);

        return "register.html";
    }

    @PostMapping("/register")
    public String register_user(@RequestParam final String name,
                                @RequestParam final String surname,
                                @RequestParam final String dob,
                                @RequestParam final String pps,
                                @RequestParam final String address,
                                @RequestParam final String phone_number,
                                @RequestParam final String email,
                                @RequestParam final String nationality,
                                @RequestParam final String password,
                                @RequestParam final String password_repeat,
                                @RequestParam final String gender,
                                @RequestParam (required = false) final Boolean mfa) {
            try {
                if (!validator.validate_user(name, surname,  dob, pps.toUpperCase(), address, phone_number, email, nationality, password, gender) || !password.equals(password_repeat)) {
                System.out.println("Invalid user!");
                return "redirect:/register?error";
            }
            User new_user = new User(name, surname, dob, pps.toUpperCase(), address, phone_number, email, nationality, bCryptPasswordEncoder.encode(password), User.LastActivity.UNVACCINATED, gender);
            if (mfa != null) {
                new_user.mfa_enabled = mfa;
            }
            new_user.setRoles(userRole());
            userRepository.save(new_user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/login";
    }

    @GetMapping("/update-roles")
    public String update_roles(){
        return "update-roles.html";
    }

    @PostMapping("/update-roles")
    public String update_roles_post(Principal principal, @RequestParam final String pps, @RequestParam String newRoles) throws UserNotFoundException {
        User user = userRepository.findByPps(principal.getName()).get();
        Set<Role> userRoles = user.getRoles();
        ArrayList<String> userRolesAsStrings = convertUserRolesToStrings(userRoles);
        if(userRolesAsStrings.contains("ADMIN")){
            User userToUpdate = userRepository.findByPps(pps).orElseThrow(() -> new UserNotFoundException(pps));
            Set<Role> userToUpdateRoles = userToUpdate.getRoles();
            String[] roleStrings = newRoles.split(" ", 0);
            for(String str : roleStrings){
                userToUpdateRoles.add(new Role(str));
            }
            userToUpdate.setRoles(userToUpdateRoles);
            userRepository.save(userToUpdate);
        }
        return "redirect:/";
    }

    @GetMapping("/portal/appointments")
    public String find_appointments(){
        return "appointments.html";
    }

    @GetMapping("/portal/update-appointments")
    public String update_appointment(){
        return "update-appointments.html";
    }

    @PostMapping("/add/vaccination")
    public String add_vaccination(@RequestParam final String pps, @RequestParam String vaccine_given, @RequestParam String vaccine_type) throws UserNotFoundException {
        User user = userRepository.findByPps(pps).orElseThrow(() -> new UserNotFoundException(pps));
        List<Appointment> appointments = appointmentsRepository.findByUser(user).stream().filter(appointment -> !appointment.getComplete()).collect(Collectors.toList());
        VaccinationCenter vaccinationCenter = null;
        boolean first_dose = user.getLastactivity() == User.LastActivity.FIRST_DOSE_APPT && vaccine_given.equals("First Dose");
        boolean second_dose = user.getLastactivity() == User.LastActivity.SECOND_DOSE_APPT && vaccine_given.equals("Second Dose");
        if(first_dose || second_dose) {
            for(Appointment appt : appointments) {
                if (appt.getDoseDetails().equals(vaccine_given)){
                    appt.setComplete(true);
                    appointmentsRepository.save(appt);
                    vaccinationCenter = appt.getVaccinationCenter();
                }
            }
        }
        User.VaccineType type = User.VaccineType.valueOf(vaccine_type);
        if(first_dose) {
            user.setFirst_dose(type);
            user.setLastactivity(User.LastActivity.FIRST_DOSE_RECEIVED);

            boolean apptFound = false;
            int counter = 0;

            Instant now = Instant.now(); // Current date
            Instant futureAppt = now.plus(Duration.ofDays(21));
            Date apptDate = Date.from(futureAppt);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String dateStr = formatter.format(apptDate);

            // Try to find an appointment 5 times
            while(!apptFound && counter < 5) {
                if (vaccinationCenter == null){
                    vaccinationCenter = VaccinationCenter.DUBLIN;
                }
                List<Appointment> appointmentsFuture = appointmentsRepository.findByDateAndVaccinationCenter(dateStr, vaccinationCenter);
                if (appointmentsFuture.size() == 0) {
                    generateAppointments(dateStr, vaccinationCenter);
                }
                appointmentsFuture = appointmentsRepository.findByDateAndAvailableAndVaccinationCenter(dateStr, true, vaccinationCenter);

                if (appointmentsFuture.size() > 1){
                    Appointment appointment = appointmentsFuture.get(0);
                    appointment.setAppointmentType(Appointment.AppointmentType.SECOND_DOSE);
                    appointment.setUser(user);
                    user.setLastactivity(User.LastActivity.SECOND_DOSE_APPT);
                    appointmentsRepository.save(appointment);
                    apptFound = true;
                }
                else{
                    futureAppt = now.plus(Duration.ofDays(1));
                    apptDate = Date.from(futureAppt);
                    dateStr = formatter.format(apptDate);
                    counter++;
                }
            }
        } else if(second_dose) {
            user.setSecond_dose(type);
            user.setLastactivity(User.LastActivity.SECOND_DOSE_RECEIVED);
        }

        userRepository.save(user);
        return "redirect:/";
    }

    @MessageMapping("/appointment-registration")
    @SendTo("/topic/updates")
    public AppointmentAvailability send(final CheckAvailabilityOnDate message) throws Exception {
        String messageDate = message.getDate();
        String messageVaccinationCenter = message.getVaccinationCenter();
        VaccinationCenter vaccinationCenter = VaccinationCenter.valueOf(messageVaccinationCenter.toUpperCase());
        List<Appointment> appointments = appointmentsRepository.findByDateAndVaccinationCenter(messageDate, vaccinationCenter);
        if (appointments.size() == 0){
            generateAppointments(messageDate, vaccinationCenter);
        }
        appointments = appointmentsRepository.findByDateAndAvailableAndVaccinationCenter(messageDate, true, vaccinationCenter);
        return new AppointmentAvailability(appointments, messageDate, messageVaccinationCenter);
    }

    @PostMapping("/add/appointment")
    public String add_vaccination(Principal principal, Model model, @RequestParam final String date, @RequestParam final String time, @RequestParam final String vaccinationCenter){
        User user = userRepository.findByPps(principal.getName()).get();
        VaccinationCenter newVaccinationCenter = VaccinationCenter.valueOf(vaccinationCenter.toUpperCase());
        Appointment appointment = appointmentsRepository.findByDateAndTimeAndVaccinationCenter(date, time, newVaccinationCenter);
        if (appointment.getAvailable() == true){
            appointment.setAvailable(false);

            if(user.getLastactivity() == User.LastActivity.UNVACCINATED){
                appointment.setAppointmentType(Appointment.AppointmentType.FIRST_DOSE);
                appointment.setUser(user);
                user.setLastactivity(User.LastActivity.FIRST_DOSE_APPT);
            }else if (user.getLastactivity() == User.LastActivity.FIRST_DOSE_RECEIVED){
                Appointment previousAppointment = appointmentsRepository.findByUserAndAppointmentType(user, AppointmentType.FIRST_DOSE);
                if (checkAppointmentsAreThreeWeeksApart(previousAppointment, appointment)){
                    appointment.setUser(user);
                    appointment.setAppointmentType(Appointment.AppointmentType.SECOND_DOSE);
                    user.setLastactivity(User.LastActivity.SECOND_DOSE_APPT);
                } else{
                    appointment.setAvailable(true);
                    model.addAttribute("flash","Appointment must be atleast 3 weeks from first");
                    return "redirect:/";
                }

            } else{
                appointment.setAvailable(true);
                model.addAttribute("flash","Appointment not created due to error");
                return "redirect:/";
            }
            appointmentsRepository.save(appointment);
        }
        model.addAttribute("flash","Appointment created");
        return "redirect:/";
    }

    @PostMapping("/cancel/appointment")
    public String cancel_appointment(Principal principal, @RequestParam final long id) {
        User user = userRepository.findByPps(principal.getName()).get();
        Appointment appointment = appointmentsRepository.findById(id).get();
        if (appointment.getUser() != user) {
            return "redirect:/";
        }
        appointment.setAvailable(true);
        appointment.setUser(null);
        if (user.getLastactivity() == User.LastActivity.FIRST_DOSE_APPT) {
            user.setLastactivity(User.LastActivity.UNVACCINATED);
        }
        if (user.getLastactivity() == User.LastActivity.SECOND_DOSE_APPT) {
            user.setLastactivity(User.LastActivity.FIRST_DOSE_RECEIVED);
        }
        appointmentsRepository.save(appointment);
        userRepository.save(user);
        return "redirect:/";
    }

    private static String QR_PREFIX =
            "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

    public String generateQRUrl(User user) throws UnsupportedEncodingException {
        return QR_PREFIX + URLEncoder.encode(String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                "HSE COVID-19 Portal", user.getPps(), user.getTotp_secret(), "HSE COVID-19 Portal"),
                StandardCharsets.UTF_8);
    }

    private Set<Role> userRole() {
        HashSet<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        return roles;
    }

    private ArrayList<String> convertUserRolesToStrings(Set<Role> userRoles){
        ArrayList<String> values = new ArrayList<String>();
        for (Role role : userRoles){
            values.add(role.getName());
        }
        return values;
    }

    private String formatLastActivity(User.LastActivity activity) {
        switch (activity) {
            case UNVACCINATED:
                return "Unvaccinated - No Vaccination Appointments Booked";
            case FIRST_DOSE_APPT:
                return "Vaccination Appointment for First Dose Booked";
            case FIRST_DOSE_RECEIVED:
                return "First Dose administered - No Second Dose Appointment Booked";
            case SECOND_DOSE_APPT:
                return "First Dose administered - Second Dose Appointment Booked";
            case SECOND_DOSE_RECEIVED:
                return "Second Dose administered - Fully Vaccinated";
            default:
                return "Invalid Last Activity";

        }
    }

    private void generateAppointments(String date, VaccinationCenter vaccinationCenter){
        for(int i = 9; i < 17; i++){
            Appointment a = new Appointment(date, String.valueOf(i)+":00", vaccinationCenter);
            Appointment b = new Appointment(date, String.valueOf(i)+":15", vaccinationCenter);
            Appointment c = new Appointment(date, String.valueOf(i)+":30", vaccinationCenter);
            Appointment d = new Appointment(date, String.valueOf(i)+":45", vaccinationCenter);
            appointmentsRepository.save(a);
            appointmentsRepository.save(b);
            appointmentsRepository.save(c);
            appointmentsRepository.save(d);
        }
    }

    private Boolean checkAppointmentsAreThreeWeeksApart(Appointment prev, Appointment upcoming){
        String prevDate = prev.getDate();
        String newDate = upcoming.getDate();
        try {
            Date prevDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(prevDate);
            Date newDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(newDate);
            long diffInMillies = Math.abs(newDate2.getTime() - prevDate2.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            return (diff >= 21);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
