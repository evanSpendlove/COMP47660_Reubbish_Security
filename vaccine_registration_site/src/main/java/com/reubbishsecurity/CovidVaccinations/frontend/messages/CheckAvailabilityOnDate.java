package com.reubbishsecurity.CovidVaccinations.frontend.messages;


public class CheckAvailabilityOnDate {
    private String date;
    private String vaccinationCenter;

    public CheckAvailabilityOnDate(){}

    public CheckAvailabilityOnDate(String date, String vaccinationCenter){
        this.date = date;
        this.vaccinationCenter = vaccinationCenter;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getVaccinationCenter(){
        return this.vaccinationCenter;
    }

    public void setVaccinationCenter(String vaccinationCenter){
        this.vaccinationCenter = vaccinationCenter;
    }

    @Override
    public String toString() {
        return "CheckAvailabilityOnDate{" +
                "Date='" + date + "\'" +
                ",VaccinationCenter='" + vaccinationCenter + "\'" +
                '}';
    }
}

