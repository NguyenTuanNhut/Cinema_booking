package com.cinema.booking.controller;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    @GetMapping
    public String listMovies(Model model) {
        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        return "movies/list";
    }

    @GetMapping("/{id}")
    public String movieDetails(@PathVariable Long id, Model model) {
        Movie movie = movieRepository.findById(id).orElseThrow();
        List<Showtime> showtimes = showtimeRepository.findByMovieAndStartTimeAfter(movie, LocalDateTime.now());
        
        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);
        return "movies/details";
    }
}
