package com.gym.dto;

import com.gym.entity.Subscription;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        Long memberId,
        String memberName,
        String planName,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        LocalDateTime createdAt
) {
    public static SubscriptionResponse fromEntity(Subscription subscription) {
        String memberName = subscription.getMember() != null
                ? subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName()
                : null;
        Long memberId = subscription.getMember() != null
                ? subscription.getMember().getId()
                : null;

        return new SubscriptionResponse(
                subscription.getId(),
                memberId,
                memberName,
                subscription.getPlanName(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.isActive(),
                subscription.getCreatedAt()
        );
    }
}
