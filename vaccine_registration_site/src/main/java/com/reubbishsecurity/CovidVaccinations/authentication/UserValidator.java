package com.reubbishsecurity.CovidVaccinations.authentication;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;

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

    private String address_validation_regex = "^[a-zA-Z' -0-9.,].{1,150}$";
    private Pattern address_pattern = Pattern.compile(address_validation_regex);

    private String phone_number_validation_regex = "^(\\+353|\\+44|0)(\\s*\\d){9,12}$";
    private Pattern phone_number_pattern = Pattern.compile(phone_number_validation_regex);

    private String email_validation_regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private Pattern email_pattern = Pattern.compile(email_validation_regex, Pattern.CASE_INSENSITIVE);

    private String[] password_sequences = new String[] {"qwerty", "qaz", "wsx", "edc", "rfv", "tgb", "yhn", "ujm", "ikl", "uiop", "asdf", "ghj", "jkl", "zxc", "vbn", "bnm", "123", "234", "345", "456", "567", "678", "789", "890"};
    private HashSet<String> common_passwords = new HashSet<>();

    public UserValidator() {
        try {
            // File file = ResourceUtils.getFile("classpath:common_passwords.txt");
            System.out.println(getClass().getClassLoader().getResource("common_passwords.txt").getPath());

            /*
            File file = new File(getClass().getClassLoader().getResource("common_passwords.txt").getPath());
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                common_passwords.add(reader.nextLine());
            }
             */
            // File file = new File("BOOT-INF/classes/common_passwords.txt");
            // BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("common_passwords.txt")));
            /*
            while (reader.ready()) {
                common_passwords.add(reader.readLine());
            }
             */
            /*
            // this.getClass().getClassLoader().getResourceAsStream("com/reubbishsecurity/CovidVaccinations/common_passwords.txt");
            // File file = new File(getClass().getClassLoader().getResource("com/reubbishsecurity/CovidVaccinations/common_passwords.txt").getFile());
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                common_passwords.add(reader.nextLine());
            }
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean validate_user(String name, String surname, String dob, String pps, String address, String phone_number, String email, String nationality, String password, String gender) {
        return validate_name(name) && validate_name(surname)
                && validate_date(dob) && validate_pps(pps)
                && validate_address(address) && validate_phone_number(phone_number)
                && validate_email(email) && validate_nationality(nationality)
                && validate_password(password) && validate_gender(gender);
    }

    private boolean validate_name(String name) {
        if (!name_pattern.matcher(name).matches()) {
            System.out.println("Invalid Name");
        }
        return name_pattern.matcher(name).matches();
    }

    private boolean validate_date(String date)  {
        System.out.println("Date = " + date);
        if (!date_pattern.matcher(date).matches()) {
            System.out.println("Invalid Date");
        }
        return date_pattern.matcher(date).matches();
    }

    private boolean validate_pps(String pps) {
        if (!pps_pattern.matcher(pps).matches()) {
            System.out.println("Invalid pps");
        }
        return pps_pattern.matcher(pps).matches();
    }

    private boolean validate_address(String address) {
        if (!address_pattern.matcher(address).matches()) {
            System.out.println("Invalid address");
        }
        return address_pattern.matcher(address).matches();
    }

    private boolean validate_phone_number(String phone_number) {
        if (!phone_number_pattern.matcher(phone_number).matches()) {
            System.out.println("Invalid phone_number");
        }
        return phone_number_pattern.matcher(phone_number).matches();
    }

    private boolean validate_email(String email) {
        if (!email_pattern.matcher(email).matches()) {
            System.out.println("Invalid email");
        }
        return email_pattern.matcher(email).matches();
    }

    private boolean validate_nationality(String nationality) {
        try {
            Nationality nat = Nationality.valueOf(nationality.toUpperCase());
            return true;
        }
        catch (IllegalArgumentException e){
            System.out.println("Invalid Nationality");
            return false;
        }
    }

    private boolean validate_gender(String gender) {
        try {
            User.Gender gen = User.Gender.valueOf(gender);
            return true;
        }
        catch (IllegalArgumentException e){
            System.out.println("Invalid gender");
            return false;
        }
    }

    private boolean validate_password(String password) {
        if (password == null) {
            System.out.println("Null password");
            return false;
        }
        if (!password_pattern.matcher(password).matches()) {
            System.out.println("Regex not matched");
            return false;
        }


        for (String seq : password_sequences) {
            if (password.contains(seq) || password.contains(new StringBuilder().append(seq).reverse().toString())) {
                System.out.println("Sequence matched");
                return false;
            }
        }

        int repetition_counter = 0;
        char seen_char = ' ';
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) == seen_char) {
                repetition_counter++;
                if (repetition_counter >= 3) {
                    System.out.println("Repetition found!");
                    return false;
                }
            } else {
                repetition_counter = 0;
                seen_char = password.charAt(i);
            }
        }

        // TODO: Screened against list of common pwds and list of pwds compromised in known security rbeaches
        if (common_passwords.contains(password)) {
            System.out.println("Found in common passwords!");
        }

        return !common_passwords.contains(password);
    }

}
