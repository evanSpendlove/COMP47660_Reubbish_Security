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
    public String index(Model model) {
        return "index.html";
    }

    @GetMapping("/login")
    public String login(){
        return "login.html";
    }

    @PostMapping("/processLogin")
    public String process_login() {
        return "index.html";
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
        return "login.html";
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
        return "index.html";
    }

    @GetMapping("/portal")
    public String portal(Principal principal, Model model){
        User user = userRepository.findByPps(principal.getName()).get();
        Set<Role> userRoles = user.getRoles();
        ArrayList<String> userRolesAsStrings = convertUserRolesToStrings(userRoles);
        model.addAttribute("userRoles", userRolesAsStrings);
        return "appointments.html";
    }

    @PostMapping("/add/vaccination")
    public String add_vaccination(@RequestParam final String pps, @RequestParam String vaccine_given) throws UserNotFoundException {
        User user = userRepository.findByPps(pps).orElseThrow(() -> new UserNotFoundException(pps));
        if(user.getLast_activity() == User.LastActivity.FIRST_DOSE_APPT) {
            user.setLast_activity(User.LastActivity.FIRST_DOSE_RECEIVED);
        } else if(user.getLast_activity() == User.LastActivity.SECOND_DOSE_APPT) {
            user.setLast_activity(User.LastActivity.SECOND_DOSE_RECEIVED);
        }
        return "index.html";
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
        if (appointment.getAvailable() == true){
            appointment.setAvailable(false);
            appointment.setUser(user);
            if(user.getLast_activity() == User.LastActivity.UNVACCINATED){
                appointment.setAppointmentType(Appointment.AppointmentType.FIRST_DOSE);
                user.setLast_activity(User.LastActivity.FIRST_DOSE_APPT);
            }else if (user.getLast_activity() == User.LastActivity.FIRST_DOSE_RECEIVED){
                appointment.setAppointmentType(Appointment.AppointmentType.SECOND_DOSE);
                user.setLast_activity(User.LastActivity.SECOND_DOSE_APPT);
            } else{
                model.addAttribute("flash","Appointment not created due to error");
                return "index.html";
            }
            appointmentsRepository.save(appointment);
        }
        model.addAttribute("flash","Appointment created");
        return "index.html";
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
}
