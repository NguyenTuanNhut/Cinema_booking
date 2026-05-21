package com.cinema.booking.repository;

import com.cinema.booking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý phim (Movie)
 * Tương tác với bảng Movie trong database
 * Hỗ trợ soft delete: phim không bị xóa khỏi DB mà chỉ đánh dấu là inactive (active = false)
 * Điều này giúp giữ dữ liệu lịch sử booking khi xóa phim
 */
public interface MovieRepository
        extends JpaRepository<Movie, Long> {

    /**
     * Lấy tất cả phim còn hoạt động (chưa bị soft delete)
     * @return Danh sách phim active, sắp xếp theo ID giảm dần
     */
    @Query("SELECT m FROM Movie m WHERE m.active = true ORDER BY m.id DESC")
    List<Movie> findAllActive();

    /**
     * Lấy phim theo ID nếu còn hoạt động
     * Dùng khi hiển thị thông tin phim cho khách hàng
     * @param id ID của phim
     * @return Optional chứa Movie nếu tìm thấy và còn active
     */
    @Query("SELECT m FROM Movie m WHERE m.id = ?1 AND m.active = true")
    Optional<Movie> findByIdIfActive(Long id);

    /**
     * Lấy tất cả phim kể cả các phim đã bị soft delete
     * Dùng cho admin xem tất cả phim (bao gồm đã xóa)
     * @return Danh sách tất cả phim, sắp xếp theo ID giảm dần
     */
    @Query("SELECT m FROM Movie m ORDER BY m.id DESC")
    List<Movie> findAllIncludingDeleted();
}