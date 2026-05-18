package com.cinema.booking.repository;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);
}