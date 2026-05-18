package com.cinema.booking.service;

import com.cinema.booking.entity.Showtime;

import java.time.LocalDateTime;

public interface ShowtimeService {
    Showtime createShowtime(Long movieId, Long roomId, LocalDateTime startTime);
}
