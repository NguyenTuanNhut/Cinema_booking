package com.cinema.booking.repository;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.User;
import com.cinema.booking.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    Optional<Booking> findByBookingCode(String bookingCode);

    java.util.List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, java.time.LocalDateTime before);

    List<Booking> findByStatus(BookingStatus status);
}