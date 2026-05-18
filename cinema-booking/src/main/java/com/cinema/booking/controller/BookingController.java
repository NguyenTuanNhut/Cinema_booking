package com.cinema.booking.controller;

import com.cinema.booking.dto.request.BookingRequest;
import com.cinema.booking.dto.response.BookingHistoryResponse;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.entity.Ticket;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.SeatRepository;
import com.cinema.booking.repository.ShowtimeRepository;
import com.cinema.booking.repository.TicketRepository;
import com.cinema.booking.repository.UserRepository;
import com.cinema.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    @GetMapping("/select-seats/{showtimeId}")
    public String selectSeats(@PathVariable Long showtimeId, Model model) {
        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow();
        
        // Lấy danh sách ID các ghế đã được đặt cho suất chiếu này
        List<Long> bookedSeatIds = ticketRepository.findByShowtimeId(showtimeId)
                .stream()
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toList());

        model.addAttribute("showtime", showtime);
        model.addAttribute("seats", seatRepository.findByRoom(showtime.getRoom()));
        model.addAttribute("bookedSeatIds", bookedSeatIds); // Truyền xuống HTML
        return "bookings/select-seats";
    }

    @GetMapping("/history")
    public String bookingHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<BookingHistoryResponse> history = bookingService.getBookingHistory(user.getId());
        model.addAttribute("history", history);
        return "bookings/history";
    }

    @PostMapping("/create")
    public String createBooking(@AuthenticationPrincipal UserDetails userDetails, 
                                @RequestParam Long showtimeId,
                                @RequestParam List<Long> seatIds) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        BookingRequest request = new BookingRequest();
        request.setShowtimeId(showtimeId);
        request.setSeatIds(seatIds);
        
        bookingService.createBooking(user.getId(), request);
        return "redirect:/bookings/history?success";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/bookings/history?cancelled";
    }
}
