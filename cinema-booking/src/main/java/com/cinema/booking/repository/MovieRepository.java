package com.cinema.booking.repository;

import com.cinema.booking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository
        extends JpaRepository<Movie, Long> {

    /**
     * Lấy tất cả phim còn active (chưa bị soft delete)
     */
    @Query("SELECT m FROM Movie m WHERE m.active = true ORDER BY m.id DESC")
    List<Movie> findAllActive();

    /**
     * Lấy phim theo ID nếu còn active
     */
    @Query("SELECT m FROM Movie m WHERE m.id = ?1 AND m.active = true")
    Optional<Movie> findByIdIfActive(Long id);

    /**
     * Lấy tất cả phim (bao gồm đã xóa) - dành cho admin view tất cả
     */
    @Query("SELECT m FROM Movie m ORDER BY m.id DESC")
    List<Movie> findAllIncludingDeleted();
}