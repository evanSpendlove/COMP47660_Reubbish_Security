package com.reubbishsecurity.CovidVaccinations.authentication.entity;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pps"}),
        @UniqueConstraint(columnNames = {"phone_number"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User {

    public enum LastActivity { UNVACCINATED, FIRST_DOSE_APPT, FIRST_DOSE_RECEIVED, SECOND_DOSE_APPT, SECOND_DOSE_RECEIVED }
    public enum Gender {MALE, FEMALE, NONBINARY, OTHER, NOT_DISCLOSED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surname;
    private Date dob; // Date of Birth
    private String pps;
    private String address;
    private String phone_number;
    private String email;
    private String nationality;
    private String password;
    private LastActivity last_activity;
    private Gender gender;

    // TODO: Add vaccination appointments using foreign key
    // Format: [id, user_id, vaccination_centre, date, dose (1st/2nd), status, vaccine given

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    public User(String name, String surname, Date dob, String pps, String address, String phone_number, String email, String nationality, String password, LastActivity last_activity, Gender gender) {
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.pps = pps;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
        this.nationality = nationality;
        this.password = password;
        this.last_activity = last_activity;
        this.gender = gender;
    }

    public User(String name, String pps, String password) {}

    public User() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPps() {
        return pps;
    }

    public void setPps(String pps_number) {
        this.pps = pps_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public LastActivity getLast_activity() {
        return last_activity;
    }

    public void setLast_activity(LastActivity lastActivity) {
        this.last_activity = lastActivity;
    }
}