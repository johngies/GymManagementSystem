package com.gym.dto;

import com.gym.entity.GymClass;

import java.time.LocalDateTime;

public record GymClassResponse(
        Long id,
        String name,
        Long trainerId,
        String trainerName,
        int capacity,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static GymClassResponse fromEntity(GymClass gymClass) {
        String trainerName = gymClass.getTrainer() != null
                ? gymClass.getTrainer().getFirstName() + " " + gymClass.getTrainer().getLastName()
                : null;
        Long trainerId = gymClass.getTrainer() != null
                ? gymClass.getTrainer().getId()
                : null;

        return new GymClassResponse(
                gymClass.getId(),
                gymClass.getName(),
                trainerId,
                trainerName,
                gymClass.getCapacity(),
                gymClass.getStartTime(),
                gymClass.getEndTime()
        );
    }
}