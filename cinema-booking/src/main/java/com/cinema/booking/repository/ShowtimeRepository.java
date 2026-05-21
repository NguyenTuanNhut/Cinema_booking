package com.cinema.booking.repository;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Room;
import com.cinema.booking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository quản lý suất chiếu (Showtime)
 * Tương tác với bảng Showtime trong database
 *
 * Suất chiếu liên kết:
 * - Một phim (Movie)
 * - Một phòng chiếu (Room)
 * - Thời gian bắt đầu và kết thúc
 */
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    /**
     * Tìm các suất chiếu bị xung đột (overlap) trong phòng và khoảng thời gian
     * Dùng khi tạo suất chiếu mới để tránh xung đột lịch
     *
     * Ví dụ: Nếu chiếu phim 1 từ 19:00-21:00, không thể tạo suất chiếu 2
     * nếu nó overlaps với thời gian này (bao gồm 15p dọn dẹp)
     *
     * @param roomId ID của phòng chiếu
     * @param startTime Thời gian bắt đầu
     * @param endTime Thời gian kết thúc
     * @return Danh sách các suất chiếu bị xung đột
     */
    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime")
    List<Showtime> findOverlappingShowtimes(@Param("roomId") Long roomId, 
                                           @Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * Tìm tất cả suất chiếu của một phòng
     * @param room Đối tượng Room
     * @return Danh sách suất chiếu trong phòng
     */
    List<Showtime> findByRoom(Room room);

    /**
     * Tìm các suất chiếu còn lại của một phim (sau thời gian hiện tại)
     * Dùng khi hiển thị danh sách suất chiếu cho khách hàng
     * @param movie Đối tượng Movie
     * @param now Thời gian hiện tại
     * @return Danh sách suất chiếu sau thời gian này
     */
    List<Showtime> findByMovieAndStartTimeAfter(Movie movie, LocalDateTime now);

    /**
     * Tìm tất cả suất chiếu sau một thời gian nhất định
     * Dùng trong scheduler để kiểm tra các suất chiếu sắp tới
     * @param time Thời gian mốc
     * @return Danh sách suất chiếu sau thời gian này
     */
    List<Showtime> findByStartTimeAfter(LocalDateTime time);
}
