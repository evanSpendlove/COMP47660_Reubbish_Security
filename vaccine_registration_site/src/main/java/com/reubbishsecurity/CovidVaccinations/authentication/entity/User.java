package com.reubbishsecurity.CovidVaccinations.authentication.entity;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;
import com.reubbishsecurity.CovidVaccinations.forum.entity.Post;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.util.Set;

@Data
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pps"}),
        @UniqueConstraint(columnNames = {"phone_number"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User {

    public enum LastActivity {
        UNVACCINATED("Unvaccinated"), FIRST_DOSE_APPT("First Dose Appointment"),
        FIRST_DOSE_RECEIVED("First Dose Received"), SECOND_DOSE_APPT("Second Dose Appointment"),
        SECOND_DOSE_RECEIVED("Second Dose Received");

        private final String text;

        LastActivity(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public enum VaccineType { MODERNA, PFIZER };

    public enum Gender {
        MALE("Male"), FEMALE("Female"), NONBINARY("Non-Binary"), OTHER("Other"), NOT_DISCLOSED("Not Disclosed");

        private final String text;

        Gender(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ColumnTransformer(
            read="AES_DECRYPT(UNHEX(name), UNHEX(SHA2('secret', 512)))",
            write="HEX(AES_ENCRYPT(?, UNHEX(SHA2('secret', 512))))"
    )
    private String name;

    @ColumnTransformer(
            read="AES_DECRYPT(UNHEX(surname), UNHEX(SHA2('secret', 512)))",
            write="HEX(AES_ENCRYPT(?, UNHEX(SHA2('secret', 512))))"
    )
    private String surname;
    @ColumnTransformer(
            read="AES_DECRYPT(UNHEX(address), UNHEX(SHA2('secret', 512)))",
            write="HEX(AES_ENCRYPT(?, UNHEX(SHA2('secret', 512))))"
    )
    private String dob; // Date of Birth
    private String pps;

    @ColumnTransformer(
            read="AES_DECRYPT(UNHEX(address), UNHEX(SHA2('secret', 512)))",
            write="HEX(AES_ENCRYPT(?, UNHEX(SHA2('secret', 512))))"
    )
    private String address;

    @ColumnTransformer(
            read="AES_DECRYPT(UNHEX(phone_number), UNHEX(SHA2('secret', 512)))",
            write="HEX(AES_ENCRYPT(?, UNHEX(SHA2('secret', 512))))"
    )
    private String phone_number;

    @ColumnTransformer(
            read="AES_DECRYPT(UNHEX(email), UNHEX(SHA2('secret', 512)))",
            write="HEX(AES_ENCRYPT(?, UNHEX(SHA2('secret', 512))))"
    )
    private String email;
    private Nationality nationality;
    private String password;
    private LastActivity lastactivity;
    private Gender gender;
    private VaccineType first_dose;
    private VaccineType second_dose;
    private boolean mfaEnabled;

    @ColumnTransformer(
            read="AES_DECRYPT(UNHEX(email), UNHEX(SHA2('secret', 512)))",
            write="HEX(AES_ENCRYPT(?, UNHEX(SHA2('secret', 512))))"
    )
    private String secret;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Appointment> appointments;

    public User(String name, String surname, String dob, String pps, String address, String phone_number, String email, String nationality, String password, LastActivity last_activity, String gender) {
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.pps = pps;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
        try {
            this.nationality = Nationality.valueOf(nationality.toUpperCase());
        }
        catch (IllegalArgumentException e){
            this.nationality = Nationality.UNKNOWN;
        }

        this.password = password;
        this.lastactivity = last_activity;

        try {
            this.gender = Gender.valueOf(gender);
        }
        catch (IllegalArgumentException e){
            this.gender = Gender.NOT_DISCLOSED;
        }

        this.mfaEnabled = false;

    }

    public User(String name, String pps, String password) {}

    public User() {}

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", pps='" + pps + '\'' +
                ", roles='" + roles + '\'' +
                ", LastActivity='" + lastactivity + '\'' +
                '}';
    }
}