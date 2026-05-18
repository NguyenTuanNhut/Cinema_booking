package com.cinema.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter
@Setter
public class Movie extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration;

    private String poster;

    private LocalDate releaseDate;

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;
}