package com.reubbishsecurity.CovidVaccinations.frontend.messages;


public class CheckAvailabilityOnDate {
    private String date;

    public CheckAvailabilityOnDate(){}

    public CheckAvailabilityOnDate(String date){
        this.date = date;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(String date){
        this.date = date;
    }

    @Override
    public String toString() {
        return "CheckAvailabilityOnDate{" +
                "Date='" + date + "\'" +
                '}';
    }
}

