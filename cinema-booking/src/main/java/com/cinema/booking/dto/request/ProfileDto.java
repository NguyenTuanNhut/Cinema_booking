package com.cinema.booking.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileDto {

    @Size(max = 100)
    private String fullName;

    @Pattern(regexp = "(^$|\\+?[0-9\\- ]{7,20})", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Size(max = 255)
    private String address;

    private LocalDate birthday;

    @Size(max = 500)
    private String avatar;
}

