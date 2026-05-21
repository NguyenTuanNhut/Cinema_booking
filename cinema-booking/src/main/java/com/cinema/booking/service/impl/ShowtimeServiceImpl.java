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

/**
 * Triển khai service quản lý suất chiếu
 * @Service: Đánh dấu đây là service bean của Spring
 * @RequiredArgsConstructor: Tự tạo constructor inject dependencies
 */
@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    // Repository để thao tác với Showtime
    private final ShowtimeRepository showtimeRepository;
    // Repository để thao tác với Movie (phim)
    private final MovieRepository movieRepository;
    // Repository để thao tác với Room (phòng)
    private final RoomRepository roomRepository;

    // Thời gian dọn dẹp sau mỗi suất chiếu (15 phút)
    private static final int CLEANING_TIME_MINUTES = 15;

    /**
     * Tạo suất chiếu mới
     * @Transactional: Đảm bảo tính nhất quán dữ liệu
     *
     * Quy trình:
     * 1. Lấy phim từ database
     * 2. Lấy phòng từ database
     * 3. Tính toán thời gian kết thúc = thời gian bắt đầu + thời lượng + 15p dọn dẹp
     * 4. Kiểm tra xung đột lịch (conflict detection)
     * 5. Tạo suất chiếu mới
     *
     * @param movieId ID của phim
     * @param roomId ID của phòng chiếu
     * @param startTime Thời gian bắt đầu chiếu
     * @return Đối tượng Showtime vừa tạo
     * @throws RuntimeException nếu phim/phòng không tồn tại hoặc có xung đột lịch
     */
    @Override
    @Transactional
    public Showtime createShowtime(Long movieId, Long roomId, LocalDateTime startTime) {
        // 1. Lấy phim từ database
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        // 2. Lấy phòng từ database
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 3. Tính toán thời gian kết thúc
        // endTime = startTime + thời lượng phim + thời gian dọn dẹp (15p)
        LocalDateTime endTime = startTime.plusMinutes(movie.getDuration() + CLEANING_TIME_MINUTES);

        // 4. Kiểm tra xung đột (Conflict detection)
        // Tìm các suất chiếu trong phòng này mà bị overlap với [startTime, endTime]
        List<Showtime> overlaps = showtimeRepository.findOverlappingShowtimes(roomId, startTime, endTime);
        if (!overlaps.isEmpty()) {
            // Nếu có xung đột, ném exception
            throw new RuntimeException("Phòng đã có lịch chiếu khác trong khoảng thời gian này (bao gồm 15p dọn dẹp)");
        }

        // 5. Tạo suất chiếu mới
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        
        return showtimeRepository.save(showtime);
    }
}
