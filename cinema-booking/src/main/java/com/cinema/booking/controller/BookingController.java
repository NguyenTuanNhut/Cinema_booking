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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final com.cinema.booking.repository.BookingRepository bookingRepository;

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
                                @RequestParam List<Long> seatIds,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BookingRequest request = new BookingRequest();
            request.setShowtimeId(showtimeId);
            request.setSeatIds(seatIds);

            bookingService.createBooking(user.getId(), request);
            // booking is created in PENDING state; inform user
            redirectAttributes.addFlashAttribute("success", "Đặt vé thành công. Vui lòng hoàn tất thanh toán hoặc chờ xác nhận.");
            return "redirect:/bookings/history";
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Không thể đặt vé. Vui lòng thử lại!";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/bookings/select-seats/" + showtimeId;
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                @PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        com.cinema.booking.entity.User requester = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        com.cinema.booking.entity.Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        boolean isOwner = booking.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() != null && "ROLE_ADMIN".equals(requester.getRole().getName());

        if (!isOwner && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền hủy đơn này");
            return "redirect:/bookings/history";
        }

        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("cancelled", true);
        } catch (RuntimeException e) {
            // Trả về message cho view thông qua flash attribute
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/bookings/history";
    }
}
