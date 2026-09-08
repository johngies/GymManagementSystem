package com.gym.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "Member ID is required")
        Long memberId,

        @NotNull(message = "Gym class ID is required")
        Long gymClassId
) {
}