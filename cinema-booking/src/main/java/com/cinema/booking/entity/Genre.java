package com.cinema.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre extends BaseEntity {

    @Column(nullable = false)
    private String name;
}