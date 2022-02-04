package com.reubbishsecurity.CovidVaccinations.authentication.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String pps_no) {
        super(String.format("User not found with pps_no : '%s'", pps_no));
    }
}


