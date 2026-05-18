package com.cinema.booking.repository;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Room;
import com.cinema.booking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime")
    List<Showtime> findOverlappingShowtimes(@Param("roomId") Long roomId, 
                                           @Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    List<Showtime> findByRoom(Room room);

    List<Showtime> findByMovieAndStartTimeAfter(Movie movie, LocalDateTime now);

    List<Showtime> findByStartTimeAfter(LocalDateTime time);
}
