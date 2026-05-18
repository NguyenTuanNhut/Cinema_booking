package com.cinema.booking.controller;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Room;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.RoomRepository;
import com.cinema.booking.repository.ShowtimeRepository;
import com.cinema.booking.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/showtimes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminShowtimeController {

    private final ShowtimeService showtimeService;
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("showtimes", showtimeRepository.findAll());
        return "admin/showtimes/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
        return "admin/showtimes/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Long movieId, 
                       @RequestParam Long roomId, 
                       @RequestParam String startTime,
                       Model model) {
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            showtimeService.createShowtime(movieId, roomId, start);
            return "redirect:/admin/showtimes";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("movies", movieRepository.findAll());
            model.addAttribute("rooms", roomRepository.findAll());
            return "admin/showtimes/form";
        }
    }
}
