package com.gym.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gym.dto.BookingRequest;
import com.gym.dto.BookingResponse;
import com.gym.entity.Booking;
import com.gym.entity.BookingStatus;
import com.gym.entity.GymClass;
import com.gym.entity.Member;
import com.gym.exception.ClassFullException;
import com.gym.exception.DuplicateResourceException;
import com.gym.exception.InvalidOperationException;
import com.gym.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private GymClassService gymClassService;

    @InjectMocks
    private BookingService bookingService;

    private Member sampleMember;
    private GymClass sampleGymClass;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        sampleMember = Member.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        sampleGymClass = GymClass.builder()
                .id(10L)
                .name("CrossFit WOD")
                .capacity(5)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();

        bookingRequest = new BookingRequest(1L, 10L);
    }

    @Test
    @DisplayName("Should successfully create booking when class has available capacity")
    void createBooking_Success_WhenCapacityAvailable() {
        // Arrange
        when(memberService.getMemberEntity(1L)).thenReturn(sampleMember);
        when(gymClassService.getGymClassEntity(10L)).thenReturn(sampleGymClass);
        when(bookingRepository.existsByMemberIdAndGymClassIdAndStatus(1L, 10L, BookingStatus.CONFIRMED))
                .thenReturn(false);
        when(bookingRepository.countByGymClassIdAndStatus(10L, BookingStatus.CONFIRMED))
                .thenReturn(3L); // 3 out of 5 spots taken

        Booking savedBooking = Booking.builder()
                .id(100L)
                .member(sampleMember)
                .gymClass(sampleGymClass)
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Act
        BookingResponse response = bookingService.createBooking(bookingRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.memberName()).isEqualTo("John Doe");
        assertThat(response.gymClassName()).isEqualTo("CrossFit WOD");
        assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw ClassFullException when gym class has reached maximum capacity")
    void createBooking_ThrowsClassFullException_WhenClassIsFull() {
        // Arrange
        when(memberService.getMemberEntity(1L)).thenReturn(sampleMember);
        when(gymClassService.getGymClassEntity(10L)).thenReturn(sampleGymClass);
        when(bookingRepository.existsByMemberIdAndGymClassIdAndStatus(1L, 10L, BookingStatus.CONFIRMED))
                .thenReturn(false);
        when(bookingRepository.countByGymClassIdAndStatus(10L, BookingStatus.CONFIRMED))
                .thenReturn(5L); // 5 out of 5 spots taken!

        // Act & Assert
        ClassFullException exception = assertThrows(
                ClassFullException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        assertThat(exception.getMessage()).contains("Class is full");
        // Verify that save was never called to protect database state
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when member already has active booking")
    void createBooking_ThrowsDuplicateResourceException_WhenMemberAlreadyBooked() {
        // Arrange
        when(memberService.getMemberEntity(1L)).thenReturn(sampleMember);
        when(gymClassService.getGymClassEntity(10L)).thenReturn(sampleGymClass);
        when(bookingRepository.existsByMemberIdAndGymClassIdAndStatus(1L, 10L, BookingStatus.CONFIRMED))
                .thenReturn(true); // Already has an active booking!

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        assertThat(exception.getMessage()).contains("already has an active booking");
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should successfully cancel an active booking")
    void cancelBooking_Success_WhenBookingIsActive() {
        // Arrange
        Booking activeBooking = Booking.builder()
                .id(100L)
                .member(sampleMember)
                .gymClass(sampleGymClass)
                .status(BookingStatus.CONFIRMED)
                .build();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(activeBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponse response = bookingService.cancelBooking(100L);

        // Assert
        assertThat(response.status()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(activeBooking);
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when trying to cancel an already cancelled booking")
    void cancelBooking_ThrowsInvalidOperationException_WhenAlreadyCancelled() {
        // Arrange
        Booking cancelledBooking = Booking.builder()
                .id(100L)
                .member(sampleMember)
                .gymClass(sampleGymClass)
                .status(BookingStatus.CANCELLED)
                .build();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(cancelledBooking));

        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> bookingService.cancelBooking(100L)
        );

        assertThat(exception.getMessage()).contains("already cancelled");
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
