package com.cinema.booking.repository;

import com.cinema.booking.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý thể loại phim (Genre)
 * Tương tác với bảng Genre trong database
 * 
 * Các thể loại phim: Action, Horror, Comedy, Drama, Sci-Fi,...
 */
public interface GenreRepository
        extends JpaRepository<Genre, Long> {
    // Sử dụng các phương thức mặc định từ JpaRepository
}