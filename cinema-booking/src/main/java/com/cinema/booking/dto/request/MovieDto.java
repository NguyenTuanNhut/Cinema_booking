package com.cinema.booking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieDto {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private String poster;
    private LocalDate releaseDate;
    private Long genreId;
}

