package com.gym.dto;

import java.time.LocalDate;

import com.gym.entity.Member;

public record MemberResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
    public static MemberResponse fromEntity(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getPhone(),
                member.getDateOfBirth()
        );
    }
}