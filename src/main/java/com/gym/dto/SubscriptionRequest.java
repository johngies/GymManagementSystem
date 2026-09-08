package com.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SubscriptionRequest(
        @NotNull(message = "Member ID is required")
        Long memberId,

        @NotBlank(message = "Plan name is required")
        @Size(max = 50, message = "Plan name must not exceed 50 characters")
        String planName,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate
) {
}