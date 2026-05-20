package com.cinema.booking.service;

import com.cinema.booking.dto.request.BookingRequest;
import com.cinema.booking.dto.response.BookingHistoryResponse;
import com.cinema.booking.entity.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, BookingRequest request);
    void cancelBooking(Long bookingId);
    List<BookingHistoryResponse> getBookingHistory(Long userId);
    Booking findByBookingCode(String bookingCode);
    void confirmBooking(Long bookingId, Long staffUserId);
    void expireBooking(Long bookingId);
    List<Booking> getPendingBookings();
}
