package com.cinema.booking.repository;

import com.cinema.booking.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository
        extends JpaRepository<Genre, Long> {
}