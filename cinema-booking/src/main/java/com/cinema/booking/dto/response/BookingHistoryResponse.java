package com.cinema.booking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class BookingHistoryResponse {
    private Long bookingId;
    private String bookingCode;
    private String movieTitle;
    private LocalDateTime startTime;
    private String roomName;
    private List<String> seats;
    private BigDecimal totalPrice;
    private String status;
}
