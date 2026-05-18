package com.cinema.booking.entity;

import com.cinema.booking.entity.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tickets",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "showtime_id",
                                "seat_id"
                        }
                )
        }
)
@Getter
@Setter
public class Ticket extends BaseEntity {

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TicketStatus status =
            TicketStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
}