package com.reubbishsecurity.CovidVaccinations.frontend.messages;
import java.util.Date;
import java.util.List;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment;
public class AppointmentAvailability {
    private List<Appointment> availableTimes;
    private String Date;
    private String vaccinationCenter;

    public AppointmentAvailability() {}

    public AppointmentAvailability(List<Appointment> availableTimes, String Date, String vaccinationCenter){
        this.availableTimes = availableTimes;
        this.Date = Date;
        this.vaccinationCenter = vaccinationCenter;
    }

    public List<Appointment> getAvailableTimes(){
        return this.availableTimes;
    }

    public void setAvailableTimes(List<Appointment> newValue){
        this.availableTimes = newValue;
    }

    public String getDate(){
        return this.Date;
    }

    public void setDate(String newValue){
        this.Date = newValue;
    }

    public String getVaccinationCenter(){
        return this.vaccinationCenter;
    }

    public void setVaccinationCenter(String vaccinationCenter){
        this.vaccinationCenter = vaccinationCenter;
    }
}
