package com.cinema.booking.repository;

import com.cinema.booking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository
        extends JpaRepository<Movie, Long> {
}