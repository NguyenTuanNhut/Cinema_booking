package com.cinema.booking.controller;

import com.cinema.booking.entity.Genre;
import com.cinema.booking.entity.Movie;
import com.cinema.booking.repository.GenreRepository;
import com.cinema.booking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/movies")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        return "admin/movies/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movies/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Movie movie) {
        movieRepository.save(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieRepository.findById(id).orElseThrow();
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movies/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        movieRepository.deleteById(id);
        return "redirect:/admin/movies";
    }
}
