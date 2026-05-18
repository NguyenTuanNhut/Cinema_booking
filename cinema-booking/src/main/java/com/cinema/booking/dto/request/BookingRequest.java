package com.cinema.booking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingRequest {
    private Long showtimeId;
    private List<Long> seatIds;
}
