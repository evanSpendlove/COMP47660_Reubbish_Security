package com.reubbishsecurity.CovidVaccinations.frontend.repository;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment.AppointmentType;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment.VaccinationCenter;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long>{
    List<Appointment> findByDateAndVaccinationCenter(String date, VaccinationCenter vaccinationCenter);
    List<Appointment> findByDateAndAvailableAndVaccinationCenter(String date, Boolean available, VaccinationCenter vaccinationCenter);
    Appointment findByDateAndTimeAndVaccinationCenter(String date, String time, VaccinationCenter vaccinationCenter);
    List<Appointment> findByUser(User user);
    Appointment findByUserAndAppointmentType(User user, AppointmentType appointmentType);
}

