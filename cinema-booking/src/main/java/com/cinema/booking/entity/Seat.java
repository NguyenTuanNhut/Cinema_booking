package com.cinema.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seats")
@Getter
@Setter
public class Seat extends BaseEntity {

    private String seatNumber;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}