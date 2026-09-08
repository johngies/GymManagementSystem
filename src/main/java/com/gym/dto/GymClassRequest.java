package com.gym.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GymClassRequest(
        @NotBlank(message = "Class name is required")
        @Size(max = 100, message = "Class name must not exceed 100 characters")
        String name,

        @NotNull(message = "Trainer ID is required")
        Long trainerId,

        @Min(value = 1, message = "Capacity must be at least 1")
        int capacity,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        LocalDateTime endTime
) {
}