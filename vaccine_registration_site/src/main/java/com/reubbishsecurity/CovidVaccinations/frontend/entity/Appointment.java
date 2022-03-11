package com.reubbishsecurity.CovidVaccinations.frontend.entity;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "appointments")
public class Appointment {

    public enum AppointmentType { NOTSET, FIRST_DOSE, SECOND_DOSE }

    public enum VaccinationCenter { DUBLIN, CORK, LIMERICK, GALWAY }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private VaccinationCenter vaccinationCenter;

    private String date;

    private String time;

    private Boolean available;

    private Boolean complete;

    private AppointmentType appointmentType; 

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Appointment() {}

    public Appointment(String date, String time, VaccinationCenter vaccinationCenter){
        this.date = date;
        this.time = time;
        this.available = true;
        this.complete = false;
        this.appointmentType = AppointmentType.NOTSET;
        this.vaccinationCenter = vaccinationCenter;
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