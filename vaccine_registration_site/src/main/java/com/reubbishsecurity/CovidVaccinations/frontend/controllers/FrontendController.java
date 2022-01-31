package com.reubbishsecurity.CovidVaccinations.frontend.controllers;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.text.SimpleDateFormat;

@Controller
public class FrontendController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "register.html";
    }

    // TODO: Add styling according to hse theme
    // TODO: Use css
    // TODO: Add input validation
    @PostMapping("/register")
    public RedirectView register_user(@RequestParam final String name,
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
            User new_user = new User(name, surname, dateFormatter.parse(dob), pps, address, phone_number, email, nationality, password);
            // TODO: Save user
            // userRepository.save(new_user);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new RedirectView("login.html");
    }
}
