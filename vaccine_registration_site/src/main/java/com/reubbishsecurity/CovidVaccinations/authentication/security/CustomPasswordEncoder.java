package com.reubbishsecurity.CovidVaccinations.authentication.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// TODO(evanSpendlove): Delete this class after setting up admin classes
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        System.out.println("Raw password = " + rawPassword);
        System.out.println("Bcrypt encoding of raw = " + bcrypt.encode(rawPassword));
        return rawPassword.toString().equals(encodedPassword);
    }
}
