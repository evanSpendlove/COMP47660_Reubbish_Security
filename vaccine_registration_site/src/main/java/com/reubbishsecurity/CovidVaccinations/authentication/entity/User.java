package com.reubbishsecurity.CovidVaccinations.authentication.entity;

import com.reubbishsecurity.CovidVaccinations.forum.entity.Post;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
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

    public enum LastActivity { UNVACCINATED, FIRST_DOSE_APPT, FIRST_DOSE_RECEIVED, SECOND_DOSE_APPT, SECOND_DOSE_RECEIVED }

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

    // TODO: Add vaccination appointments using foreign key
    // Format: [id, user_id, vaccination_centre, date, dose (1st/2nd), status, vaccine given

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Post> posts;

    public User(String name, String surname, Date dob, String pps, String address, String phone_number, String email, String nationality, String password, LastActivity last_activity) {
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
                '}';
    }
}