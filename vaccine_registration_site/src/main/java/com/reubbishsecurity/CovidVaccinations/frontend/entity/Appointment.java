package com.reubbishsecurity.CovidVaccinations.frontend.entity;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "appointments")
public class Appointment {

    public enum AppointmentType { NOTSET, FIRST_DOSE, SECOND_DOSE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String date;

    private String time;

    private Boolean available;

    private AppointmentType appointmentType; 

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Appointment() {}

    public Appointment(String date, String time){
        this.date = date;
        this.time = time;
        this.available = true;
        this.appointmentType = AppointmentType.NOTSET;
    }

    public int compareTo(Appointment u) {
        if (date == null || u.getDate() == null) {
            return 0;
        }
        return date.compareTo(u.getDate());
    }

    public String getDoseDetails() {
        if (appointmentType == AppointmentType.FIRST_DOSE) {
            return "First Dose";
        } else {
            return "Second Dose";
        }
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "Date='" + date + '\'' +
                ", Time='" + time + '\'' +
                ", User='" + user + '\'' +
                ", Available='" + available + '\'' +
                ", Type=" + appointmentType +
                '}';
    }
}