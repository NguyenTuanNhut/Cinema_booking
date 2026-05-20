package com.cinema.booking.service;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.enums.BookingStatus;
import com.cinema.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationCleaner {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    // Run every minute
    @Scheduled(fixedDelay = 60000)
    public void cleanExpiredReservations() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);
        List<Booking> expired = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff);
        for (Booking b : expired) {
            try {
                bookingService.expireBooking(b.getId());
            } catch (Exception e) {
                // log and continue
                System.err.println("Failed to expire booking id=" + b.getId() + ": " + e.getMessage());
            }
        }
    }
}

