package com.cinema.booking.service.impl;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Room;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.RoomRepository;
import com.cinema.booking.repository.ShowtimeRepository;
import com.cinema.booking.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    private static final int CLEANING_TIME_MINUTES = 15;

    @Override
    @Transactional
    public Showtime createShowtime(Long movieId, Long roomId, LocalDateTime startTime) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Tính toán endTime = startTime + duration + cleaning time
        LocalDateTime endTime = startTime.plusMinutes(movie.getDuration() + CLEANING_TIME_MINUTES);

        // Kiểm tra xung đột (Conflict detection)
        List<Showtime> overlaps = showtimeRepository.findOverlappingShowtimes(roomId, startTime, endTime);
        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Phòng đã có lịch chiếu khác trong khoảng thời gian này (bao gồm 15p dọn dẹp)");
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        
        return showtimeRepository.save(showtime);
    }
}
