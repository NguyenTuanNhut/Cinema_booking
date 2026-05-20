package com.cinema.booking.controller;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
@RequiredArgsConstructor
public class StaffController {

    private final BookingService bookingService;
    private final com.cinema.booking.repository.UserRepository userRepository;

    @GetMapping
    public String dashboard() {
        return "staff/index";
    }

    /**
     * Danh sách đơn đang chờ xác nhận (PENDING)
     */
    @GetMapping("/pending-bookings")
    public String pendingBookings(Model model) {
        List<Booking> pendingBookings = bookingService.getPendingBookings();
        model.addAttribute("pendingBookings", pendingBookings);
        return "staff/bookings/pending-list";
    }

    /**
     * Form tìm đơn theo mã (search)
     */
    @GetMapping("/bookings")
    public String bookingSearch(@RequestParam(value = "code", required = false) String code, Model model) {
        if (code != null && !code.isBlank()) {
            try {
                Booking booking = bookingService.findByBookingCode(code.trim());
                model.addAttribute("booking", booking);
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "staff/bookings/search";
    }

    /**
     * In vé / trang printable
     */
    @GetMapping("/bookings/print")
    public String printBooking(@RequestParam("code") String code, Model model) {
        Booking booking = bookingService.findByBookingCode(code.trim());
        model.addAttribute("booking", booking);
        return "staff/bookings/print";
    }

    @org.springframework.web.bind.annotation.PostMapping("/bookings/confirm/{id}")
    public String confirmBooking(@PathVariable Long id,
                                 @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            com.cinema.booking.entity.User staff = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            bookingService.confirmBooking(id, staff.getId());
            redirectAttributes.addFlashAttribute("success", "Xác nhận thanh toán thành công!");
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Lỗi xác nhận thanh toán";
            redirectAttributes.addFlashAttribute("error", errorMessage);
        }
        return "redirect:/staff/bookings";
    }
}


