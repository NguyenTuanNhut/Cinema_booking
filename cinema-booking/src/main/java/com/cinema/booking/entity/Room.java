package com.cinema.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class Room extends BaseEntity {

    private String name;

    private Integer totalSeats;
}