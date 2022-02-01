package com.reubbishsecurity.CovidVaccinations.frontend.controllers;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.Role;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@Controller
public class FrontendController {

    @Autowired
    UserRepository userRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @PostMapping("/processLogin")
    public String process_login() {
        return "index.html";
    }

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }

    // TODO: Add some validation on the date picker.
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
                                      @RequestParam final String password_repeat) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-DD");
            User new_user = new User(name, surname, dateFormatter.parse(dob), pps.toUpperCase(), address, phone_number, email, nationality.toLowerCase(), bCryptPasswordEncoder.encode(password));
            new_user.setRoles(userRole());
            userRepository.save(new_user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "login.html";
    }

    private Set<Role> userRole() {
        HashSet<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        return roles;
    }
}
