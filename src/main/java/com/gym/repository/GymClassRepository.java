package com.gym.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.entity.GymClass;

@Repository
public interface GymClassRepository extends JpaRepository<GymClass, Long> {

    List<GymClass> findByTrainerId(Long trainerId);

    List<GymClass> findByStartTimeAfter(LocalDateTime dateTime);

    List<GymClass> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}

