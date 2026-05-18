package com.cinema.booking.repository;

import com.cinema.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository
        extends JpaRepository<Room, Long> {
}