package com.gym.dto;

import com.gym.entity.Trainer;

public record TrainerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String specialty
) {
    public static TrainerResponse fromEntity(Trainer trainer) {
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getEmail(),
                trainer.getPhone(),
                trainer.getSpecialty()
        );
    }
}
