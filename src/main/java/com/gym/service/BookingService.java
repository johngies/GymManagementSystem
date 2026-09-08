package com.gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.dto.BookingRequest;
import com.gym.dto.BookingResponse;
import com.gym.entity.Booking;
import com.gym.entity.BookingStatus;
import com.gym.entity.GymClass;
import com.gym.entity.Member;
import com.gym.exception.ClassFullException;
import com.gym.exception.DuplicateResourceException;
import com.gym.exception.InvalidOperationException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MemberService memberService;
    private final GymClassService gymClassService;

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    public BookingResponse getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(BookingResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    public List<BookingResponse> getBookingsByMember(Long memberId) {
        return bookingRepository.findByMemberId(memberId)
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    public List<BookingResponse> getBookingsByClass(Long gymClassId) {
        return bookingRepository.findByGymClassId(gymClassId)
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Member member = memberService.getMemberEntity(request.memberId());

        GymClass gymClass = gymClassService.getGymClassEntity(request.gymClassId());

        if (bookingRepository.existsByMemberIdAndGymClassIdAndStatus(
                member.getId(), gymClass.getId(), BookingStatus.CONFIRMED)) {
            throw new DuplicateResourceException(
                    "Member with id " + member.getId() + " already has an active booking for class '" + gymClass.getName() + "'");
        }

        long activeBookings = bookingRepository.countByGymClassIdAndStatus(gymClass.getId(), BookingStatus.CONFIRMED);
        if (activeBookings >= gymClass.getCapacity()) {
            throw new ClassFullException(
                    "Cannot book class '" + gymClass.getName() + "'. Class is full (Capacity: " + gymClass.getCapacity() + ")");
        }

        Booking booking = Booking.builder()
                .member(member)
                .gymClass(gymClass)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return BookingResponse.fromEntity(savedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidOperationException("Booking with id " + id + " is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updatedBooking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }
}