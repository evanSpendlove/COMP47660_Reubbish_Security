package com.reubbishsecurity.CovidVaccinations.authentication;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Pattern;

public class UserValidator {

    private String password_validation_regex = "^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=!-_;:/*~<>?.{}])"
            + "(?=\\S+$).{15,50}$";
    private Pattern password_pattern = Pattern.compile(password_validation_regex);

    private String name_validation_regex = "^[a-zA-Z'-].{1,50}$";
    private Pattern name_pattern = Pattern.compile(name_validation_regex);

    private String date_validation_regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
    private Pattern date_pattern = Pattern.compile(date_validation_regex);

    private String pps_validation_regex = "[0-9]{7}[a-zA-z]{1,2}";
    private Pattern pps_pattern = Pattern.compile(pps_validation_regex);

    private String address_validation_regex = "[a-zA-Z0-9' -.,]{1,150}";
    private Pattern address_pattern = Pattern.compile(address_validation_regex);

    private String phone_number_validation_regex = "^(\\+353|\\+44|0)(\\s*\\d){9,12}$";
    private Pattern phone_number_pattern = Pattern.compile(phone_number_validation_regex);

    private String email_validation_regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private Pattern email_pattern = Pattern.compile(email_validation_regex, Pattern.CASE_INSENSITIVE);

    private String[] password_sequences = new String[] {"qwerty", "qaz", "wsx", "edc", "rfv", "tgb", "yhn", "ujm", "ikl", "uiop", "asdf", "ghj", "jkl", "zxc", "vbn", "bnm", "123", "234", "345", "456", "567", "678", "789", "890"};
    private HashSet<String> common_passwords = new HashSet<>();

    public UserValidator() {
        try {
            ClassPathResource resource = new ClassPathResource("/common_passwords.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            while (reader.ready()) {
                common_passwords.add(reader.readLine());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean validate_user(String name, String surname, String dob, String pps, String address, String phone_number, String email, String nationality, String password, String gender) {
        boolean b_name = validate_name(name);
        boolean b_surname = validate_name(surname);
        boolean b_date =  validate_date(dob);
        boolean b_pps =  validate_pps(pps);
        boolean b_addr =  validate_address(address);
        boolean b_phone =  validate_phone_number(phone_number);
        boolean b_email =  validate_email(email);
        boolean b_nationality =  validate_nationality(nationality);
        boolean b_password =  validate_password(password);
        boolean b_gender =  validate_gender(gender);

        System.out.println("Name: " + b_name + ", surname = " + b_surname + ", date = " + b_date + ", pps = " + b_pps + ", addr = " + b_addr + ", phone = "  + b_phone + ", email = " + b_email + ", nationality = " + b_nationality + ", gender = " + b_gender + ", password = " + b_password);

        return validate_name(name) && validate_name(surname)
                && validate_date(dob) && validate_pps(pps)
                && validate_address(address) && validate_phone_number(phone_number)
                && validate_email(email) && validate_nationality(nationality)
                && validate_password(password) && validate_gender(gender);
    }

    private boolean validate_name(String name) {
        return name_pattern.matcher(name).matches();
    }

    private boolean validate_date(String date)  {
        return date_pattern.matcher(date).matches();
    }

    private boolean validate_pps(String pps) {
        return pps_pattern.matcher(pps).matches();
    }

    private boolean validate_address(String address) {
        return address_pattern.matcher(address).matches();
    }

    private boolean validate_phone_number(String phone_number) {
        return phone_number_pattern.matcher(phone_number).matches();
    }

    private boolean validate_email(String email) {
        return email_pattern.matcher(email).matches();
    }

    private boolean validate_nationality(String nationality) {
        try {
            Nationality nat = Nationality.valueOf(nationality.toUpperCase());
            return true;
        }
        catch (IllegalArgumentException e){
            return false;
        }
    }

    private boolean validate_gender(String gender) {
        try {
            User.Gender gen = User.Gender.valueOf(gender);
            return true;
        }
        catch (IllegalArgumentException e){
            return false;
        }
    }

    private boolean validate_password(String password) {
        if (password == null) {
            return false;
        }
        if (!password_pattern.matcher(password).matches()) {
            return false;
        }


        for (String seq : password_sequences) {
            if (password.contains(seq) || password.contains(new StringBuilder().append(seq).reverse().toString())) {
                return false;
            }
        }

        int repetition_counter = 0;
        char seen_char = ' ';
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) == seen_char) {
                repetition_counter++;
                if (repetition_counter >= 3) {
                    return false;
                }
            } else {
                repetition_counter = 0;
                seen_char = password.charAt(i);
            }
        }

        return !common_passwords.contains(password);
    }
}
