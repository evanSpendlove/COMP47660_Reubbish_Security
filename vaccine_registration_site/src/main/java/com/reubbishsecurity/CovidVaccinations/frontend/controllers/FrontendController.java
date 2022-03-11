package com.reubbishsecurity.CovidVaccinations.frontend.controllers;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.Role;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;
import com.reubbishsecurity.CovidVaccinations.authentication.exception.UserNotFoundException;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import com.reubbishsecurity.CovidVaccinations.frontend.messages.AppointmentAvailability;
import com.reubbishsecurity.CovidVaccinations.frontend.messages.CheckAvailabilityOnDate;
import com.reubbishsecurity.CovidVaccinations.frontend.repository.AppointmentsRepository;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment.AppointmentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import java.security.Principal;
import org.springframework.ui.Model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.stream.Collectors;
=======
import java.util.concurrent.TimeUnit;
>>>>>>> Added checking appointment is atleast 3 weeks away and fixed updating vaccinations

@Controller
public class FrontendController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AppointmentsRepository appointmentsRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private static final String[] nationalities = Arrays.stream(Nationality.values()).map(Enum::name).toArray(String[]::new);
    private static final String[] genders = Arrays.stream(User.Gender.values()).map(Enum::name).toArray(String[]::new);

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        User user = userRepository.findByPps(principal.getName()).get();
        List<Appointment> appts = appointmentsRepository.findByUser(user).stream().filter(appointment -> appointment.getComplete() == false).collect(Collectors.toList());
        System.out.println(appointmentsRepository.findByUser(user));
        System.out.println(appts);
        model.addAttribute("name", user.getName());
        model.addAttribute("appointments", appts);
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
                                @RequestParam final String gender) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-DD");
            User new_user = new User(name, surname, dateFormatter.parse(dob), pps.toUpperCase(), address, phone_number, email, nationality, bCryptPasswordEncoder.encode(password), User.LastActivity.UNVACCINATED, gender);
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
    public String add_vaccination(@RequestParam final String pps, @RequestParam String vaccine_given) throws UserNotFoundException {
        User user = userRepository.findByPps(pps).orElseThrow(() -> new UserNotFoundException(pps));
<<<<<<< HEAD
        List<Appointment> appointments = appointmentsRepository.findByUser(user).stream().filter(appointment -> appointment.getComplete() == false).collect(Collectors.toList());
        for(Appointment appt : appointments) {
            if (appt.getDoseDetails() == vaccine_given) {
                appt.setComplete(true);
                appointmentsRepository.save(appt);
            }
        }
        if(user.getLastactivity() == User.LastActivity.FIRST_DOSE_APPT && vaccine_given == "First Dose") {
=======
        if(user.getLastactivity() == User.LastActivity.FIRST_DOSE_APPT && vaccine_given.equals("First Dose")) {
            System.out.println("updated first dose received");
>>>>>>> Added checking appointment is atleast 3 weeks away and fixed updating vaccinations
            user.setLastactivity(User.LastActivity.FIRST_DOSE_RECEIVED);
            userRepository.save(user);
        } else if(user.getLastactivity() == User.LastActivity.SECOND_DOSE_APPT && vaccine_given.equals("Second Dose")) {
            System.out.println("updated second dose received");
            user.setLastactivity(User.LastActivity.SECOND_DOSE_RECEIVED);
            userRepository.save(user);
        }
        return "redirect:/";
    }

    @MessageMapping("/appointment-registration")
    @SendTo("/topic/updates")
    public AppointmentAvailability send(final CheckAvailabilityOnDate message) throws Exception {
        String messageDate = message.getDate();
        List<Appointment> appointments = appointmentsRepository.findByDate(messageDate);
        if (appointments.size() == 0){
            generateAppointments(messageDate);
        }
        appointments = appointmentsRepository.findByDateAndAvailable(messageDate, true);
        return new AppointmentAvailability(appointments, messageDate);
    }

    @PostMapping("/add/appointment")
    public String add_vaccination(Principal principal, Model model, @RequestParam final String date, @RequestParam final String time){
        User user = userRepository.findByPps(principal.getName()).get();
        Appointment appointment = appointmentsRepository.findByDateAndTime(date, time);
        System.out.println(user);
        System.out.println("gets here 1");
        if (appointment.getAvailable() == true){
            appointment.setAvailable(false);
            System.out.println("gets here 2");
            
            if(user.getLastactivity() == User.LastActivity.UNVACCINATED){
                appointment.setAppointmentType(Appointment.AppointmentType.FIRST_DOSE);
                appointment.setUser(user);
                user.setLastactivity(User.LastActivity.FIRST_DOSE_APPT);
            }else if (user.getLastactivity() == User.LastActivity.FIRST_DOSE_RECEIVED){
                System.out.println(user);
                Appointment previousAppointment = appointmentsRepository.findByUserAndAppointmentType(user, AppointmentType.FIRST_DOSE);
                System.out.println(previousAppointment);
                if (checkAppointmentsAreThreeWeeksApart(previousAppointment, appointment)){
                    System.out.println("gets here 3");
                    appointment.setUser(user);
                    appointment.setAppointmentType(Appointment.AppointmentType.SECOND_DOSE);
                    System.out.println("gets here 4");
                    user.setLastactivity(User.LastActivity.SECOND_DOSE_APPT);
                } else{
                    System.out.println("gets here 5");
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
            user.setLastactivity(User.LastActivity.FIRST_DOSE_RECEIVED);
        }
        if (user.getLastactivity() == User.LastActivity.SECOND_DOSE_APPT) {
            user.setLastactivity(User.LastActivity.SECOND_DOSE_RECEIVED);
        }
        appointmentsRepository.save(appointment);
        userRepository.save(user);
        return "redirect:/";
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

    private void generateAppointments(String date){
        for(int i = 9; i < 17; i++){
            Appointment a = new Appointment(date, String.valueOf(i)+":00");
            Appointment b = new Appointment(date, String.valueOf(i)+":15");
            Appointment c = new Appointment(date, String.valueOf(i)+":30");
            Appointment d = new Appointment(date, String.valueOf(i)+":45");
            appointmentsRepository.save(a);
            appointmentsRepository.save(b);
            appointmentsRepository.save(c);
            appointmentsRepository.save(d);
        }
    }

    private Boolean checkAppointmentsAreThreeWeeksApart(Appointment prev, Appointment upcoming){
        System.out.println("gets here 6");
        String prevDate = prev.getDate();
        String newDate = upcoming.getDate();
        try {
            System.out.println("gets here 7");
            Date prevDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(prevDate);
            Date newDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(newDate);
            System.out.println("gets here 8");
            long diffInMillies = Math.abs(newDate2.getTime() - prevDate2.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            System.out.println("gets here 9");
            System.out.println(diff);
            System.out.println(prevDate2);
            System.out.println(newDate2);
            System.out.println(prevDate);
            System.out.println(newDate);

            return (diff >= 21);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        

        
    }
}
