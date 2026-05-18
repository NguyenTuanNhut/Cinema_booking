package com.cinema.booking.repository;

import com.cinema.booking.entity.Room;
import com.cinema.booking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository
        extends JpaRepository<Seat, Long> {

    List<Seat> findByRoom(Room room);
}