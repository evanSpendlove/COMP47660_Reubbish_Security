package com.reubbishsecurity.CovidVaccinations.frontend.repository;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment;
import com.reubbishsecurity.CovidVaccinations.frontend.entity.Appointment.AppointmentType;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long>{
    List<Appointment> findByDate(String date);
    List<Appointment> findByDateAndAvailable(String date, Boolean available);
    Appointment findByDateAndTime(String date, String time);
    List<Appointment> findByUser(User user);
    Appointment findByUserAndAppointmentType(User user, AppointmentType appointmentType);
}

