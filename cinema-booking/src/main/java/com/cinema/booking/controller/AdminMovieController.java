package com.cinema.booking.controller;

import com.cinema.booking.entity.Genre;
import com.cinema.booking.entity.Movie;
import com.cinema.booking.repository.GenreRepository;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/movies")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final FileUploadService fileUploadService;

    @GetMapping
    public String list(Model model) {
        // Hiển thị tất cả movies (kể cả đã xóa) để admin quản lý
        List<Movie> allMovies = movieRepository.findAllIncludingDeleted();
        model.addAttribute("movies", allMovies);
        return "admin/movies/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("movieDto", new com.cinema.booking.dto.request.MovieDto());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movies/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("movieDto") com.cinema.booking.dto.request.MovieDto movieDto) {
        Movie movie;
        if (movieDto.getId() != null) {
            movie = movieRepository.findById(movieDto.getId()).orElse(new Movie());
        } else {
            movie = new Movie();
        }

        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setDuration(movieDto.getDuration());
        movie.setPoster(movieDto.getPoster());
        movie.setReleaseDate(movieDto.getReleaseDate());

        if (movieDto.getGenreId() != null) {
            Genre g = genreRepository.findById(movieDto.getGenreId()).orElse(null);
            movie.setGenre(g);
        }

        movieRepository.save(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieRepository.findByIdIfActive(id)
                .orElseThrow(() -> new RuntimeException("Phim không tồn tại hoặc đã bị xóa"));
        com.cinema.booking.dto.request.MovieDto dto = new com.cinema.booking.dto.request.MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setPoster(movie.getPoster());
        dto.setReleaseDate(movie.getReleaseDate());
        if (movie.getGenre() != null) dto.setGenreId(movie.getGenre().getId());

        model.addAttribute("movieDto", dto);
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movies/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phim không tồn tại"));
        
        // Soft delete: đánh dấu là đã xóa thay vì xóa vật lý
        movie.setActive(false);
        movieRepository.save(movie);
        
        return "redirect:/admin/movies";
    }

    /**
     * Khôi phục phim đã bị soft delete
     */
    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phim không tồn tại"));
        
        // Khôi phục: set active = true
        movie.setActive(true);
        movieRepository.save(movie);
        
        return "redirect:/admin/movies";
    }

    /**
     * Endpoint upload file ảnh
     * Return JSON với URL của ảnh
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadPoster(@RequestParam("file") MultipartFile file) {
        try {
            String posterUrl = fileUploadService.uploadMoviePoster(file);
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("posterUrl", posterUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Lỗi upload: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
