package com.cinema.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "profiles")
@Getter
@Setter
public class Profile extends BaseEntity {

    private String fullName;

    private String phone;

    private String address;

    private LocalDate birthday;

    private String avatar;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}