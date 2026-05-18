package com.cinema.booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;

    private String fullName;

    private String phone;
}