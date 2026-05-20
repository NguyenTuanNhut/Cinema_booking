package com.cinema.booking.repository;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Seat;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    boolean existsByShowtimeAndSeat(
            Showtime showtime,
            Seat seat
    );

    List<Ticket> findByBooking(
            Booking booking
    );

    // lấy ghế đã đặt của suất chiếu
    List<Ticket> findByShowtimeId(
            Long showtimeId
    );
}