package com.reubbishsecurity.CovidVaccinations.authentication.repository;

import com.reubbishsecurity.CovidVaccinations.authentication.entity.User;
import com.reubbishsecurity.CovidVaccinations.authentication.entity.util.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPps(String pps_number);

    long countAllByNationalityAndLastactivity(Nationality nationality, User.LastActivity lastActivity);

    long countAllByGenderAndLastactivity(User.Gender gender, User.LastActivity lastActivity);

    long countAllByLastactivityAndDobBetween(User.LastActivity last_Activity, String rangeLow, String rangeHigh);
}