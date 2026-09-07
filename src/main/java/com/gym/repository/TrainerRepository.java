package com.gym.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.entity.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    List<Trainer> findBySpecialtyIgnoreCase(String specialty);

    boolean existsByEmail(String email);

    Optional<Trainer> findByEmail(String email);
}
