package com.reubbishsecurity.CovidVaccinations.forum.repository;

import com.reubbishsecurity.CovidVaccinations.forum.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {}
