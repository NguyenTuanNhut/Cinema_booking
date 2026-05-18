package com.cinema.booking.entity;

import com.cinema.booking.entity.enums.ShowtimeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
public class Showtime extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ShowtimeStatus status = ShowtimeStatus.AVAILABLE;
}