package com.gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gym.entity.Booking;
import com.gym.entity.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByGymClassIdAndStatus(Long gymClassId, BookingStatus status);

    boolean existsByMemberIdAndGymClassIdAndStatus(Long memberId, Long gymClassId, BookingStatus status);

    List<Booking> findByMemberId(Long memberId);

    List<Booking> findByGymClassId(Long gymClassId);
}
