package com.reubbishsecurity.CovidVaccinations.forum.repository;

import com.reubbishsecurity.CovidVaccinations.forum.entity.Post;
import com.reubbishsecurity.CovidVaccinations.forum.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Set<Post>> findAllByThread(Thread thread);
}
