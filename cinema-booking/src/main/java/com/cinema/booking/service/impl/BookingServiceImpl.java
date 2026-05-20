package com.cinema.booking.service.impl;

import com.cinema.booking.dto.request.BookingRequest;
import com.cinema.booking.dto.response.BookingHistoryResponse;
import com.cinema.booking.entity.*;
import com.cinema.booking.entity.enums.BookingStatus;
import com.cinema.booking.entity.enums.TicketStatus;
import com.cinema.booking.repository.*;
import com.cinema.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        Booking booking = new Booking();
        booking.setBookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setStatus(BookingStatus.PENDING); // reservation pending until payment or staff confirm
        booking.setTotalPrice(BigDecimal.ZERO);

        Booking savedBooking = bookingRepository.save(booking);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal ticketPrice = new BigDecimal("50000");

        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));

            Ticket ticket = new Ticket();
            ticket.setBooking(savedBooking);
            ticket.setShowtime(showtime);
            ticket.setSeat(seat);
            ticket.setPrice(ticketPrice);
            ticket.setStatus(com.cinema.booking.entity.enums.TicketStatus.PENDING);

            try {
                ticketRepository.save(ticket);
            } catch (Exception e) {
                throw new RuntimeException("Ghế " + seat.getSeatNumber() + " đã được đặt!");
            }

            totalPrice = totalPrice.add(ticketPrice);
        }

        savedBooking.setTotalPrice(totalPrice);
        return bookingRepository.save(savedBooking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt vé"));

        if (booking.getShowtime().getStartTime().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Chỉ có thể hủy vé trước giờ chiếu 24 tiếng");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        ticketRepository.deleteAll(tickets);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingHistoryResponse> getBookingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser(user);

        return bookings.stream().map(b -> {
            List<Ticket> tickets = ticketRepository.findByBooking(b);
            List<String> seats = tickets.stream()
                    .map(t -> t.getSeat().getSeatNumber())
                    .collect(Collectors.toList());

            return BookingHistoryResponse.builder()
                    .bookingId(b.getId())
                    .bookingCode(b.getBookingCode())
                    .movieTitle(b.getShowtime().getMovie().getTitle())
                    .startTime(b.getShowtime().getStartTime())
                    .roomName(b.getShowtime().getRoom().getName())
                    .seats(seats)
                    .totalPrice(b.getTotalPrice())
                    .status(b.getStatus().name())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Booking findByBookingCode(String bookingCode) {
        return bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã đơn: " + bookingCode));
    }

    @Override
    @Transactional
    public void confirmBooking(Long bookingId, Long staffUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // set tickets to ACTIVE
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        for (Ticket t : tickets) {
            t.setStatus(com.cinema.booking.entity.enums.TicketStatus.ACTIVE);
            ticketRepository.save(t);
        }

        // set booking status and audit
        booking.setStatus(com.cinema.booking.entity.enums.BookingStatus.CONFIRMED);
        com.cinema.booking.entity.User staff = userRepository.findById(staffUserId)
                .orElse(null);
        if (staff != null) {
            booking.setConfirmedBy(staff);
            booking.setConfirmedAt(java.time.LocalDateTime.now());
        }

        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void expireBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Only expire pending bookings
        if (booking.getStatus() != com.cinema.booking.entity.enums.BookingStatus.PENDING) return;

        // delete tickets
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        ticketRepository.deleteAll(tickets);

        booking.setStatus(com.cinema.booking.entity.enums.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatus(BookingStatus.PENDING);
    }
}
