package com.gym.dto;

import com.gym.entity.Booking;
import com.gym.entity.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long memberId,
        String memberName,
        Long gymClassId,
        String gymClassName,
        BookingStatus status,
        LocalDateTime createdAt
) {
    public static BookingResponse fromEntity(Booking booking) {
        String memberName = booking.getMember() != null
                ? booking.getMember().getFirstName() + " " + booking.getMember().getLastName()
                : null;
        Long memberId = booking.getMember() != null
                ? booking.getMember().getId()
                : null;

        String gymClassName = booking.getGymClass() != null
                ? booking.getGymClass().getName()
                : null;
        Long gymClassId = booking.getGymClass() != null
                ? booking.getGymClass().getId()
                : null;

        return new BookingResponse(
                booking.getId(),
                memberId,
                memberName,
                gymClassId,
                gymClassName,
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
