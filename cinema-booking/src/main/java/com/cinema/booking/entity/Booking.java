package com.cinema.booking.entity;

import com.cinema.booking.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking extends BaseEntity {

    private String bookingCode;

    private BigDecimal totalPrice;

    private LocalDateTime bookingTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private BookingStatus status =
            BookingStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;
}