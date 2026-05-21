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

/**
 * Triển khai service quản lý đặt vé
 * @Service: Đánh dấu đây là service bean của Spring
 * @RequiredArgsConstructor: Tự tạo constructor để inject dependencies (final fields)
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    // Các repository để thao tác với database
    private final BookingRepository bookingRepository;      // Quản lý Booking
    private final TicketRepository ticketRepository;        // Quản lý Ticket (vé)
    private final ShowtimeRepository showtimeRepository;    // Quản lý Showtime (suất chiếu)
    private final SeatRepository seatRepository;            // Quản lý Seat (ghế)
    private final UserRepository userRepository;            // Quản lý User (người dùng)

    /**
     * Tạo đơn đặt vé mới
     * @Transactional: Nếu có lỗi ở bất kỳ bước nào, rollback toàn bộ transaction
     * Quy trình:
     * 1. Lấy user từ database
     * 2. Lấy suất chiếu từ database
     * 3. Tạo đơn đặt vé mới (trạng thái PENDING)
     * 4. Lặp qua từng ghế và tạo ticket
     * 5. Cập nhật tổng giá
     */
    @Override
    @Transactional
    public Booking createBooking(Long userId, BookingRequest request) {
        // 1. Lấy người dùng từ database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Lấy suất chiếu từ database
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // 3. Tạo đơn đặt vé mới
        Booking booking = new Booking();
        booking.setBookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());  // Mã đặt vé 8 ký tự
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setStatus(BookingStatus.PENDING);   // Trạng thái chờ xác nhận
        booking.setTotalPrice(BigDecimal.ZERO);

        Booking savedBooking = bookingRepository.save(booking);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal ticketPrice = new BigDecimal("50000");  // Giá 1 vé: 50.000 VND

        // 4. Tạo ticket cho mỗi ghế được đặt
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
                // Nếu ghế đã được đặt hoặc lỗi khác, ném exception
                throw new RuntimeException("Ghế " + seat.getSeatNumber() + " đã được đặt!");
            }

            totalPrice = totalPrice.add(ticketPrice);
        }

        // 5. Cập nhật tổng giá
        savedBooking.setTotalPrice(totalPrice);
        return bookingRepository.save(savedBooking);
    }

    /**
     * Hủy đơn đặt vé
     * @Transactional: Đảm bảo tính nhất quán dữ liệu
     * Quy tắc: Chỉ hủy được nếu còn hơn 24 giờ trước khi chiếu
     */
    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt vé"));

        // Kiểm tra xem còn lại bao lâu đến giờ chiếu
        if (booking.getShowtime().getStartTime().minusHours(24).isBefore(LocalDateTime.now())) {
            // Nếu chỉ còn dưới 24 giờ, không được hủy
            throw new RuntimeException("Chỉ có thể hủy vé trước giờ chiếu 24 tiếng");
        }

        // Đánh dấu trạng thái là đã hủy
        booking.setStatus(BookingStatus.CANCELLED);
        // Xóa tất cả tickets của đơn này
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        ticketRepository.deleteAll(tickets);
        bookingRepository.save(booking);
    }

    /**
     * Lấy lịch sử đặt vé của người dùng
     * @Transactional(readOnly = true): Chỉ đọc, không cập nhật
     * Dùng khi người dùng xem lịch sử đặt vé của họ
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookingHistoryResponse> getBookingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy tất cả booking của user
        List<Booking> bookings = bookingRepository.findByUser(user);

        // Convert mỗi Booking thành BookingHistoryResponse để hiển thị
        return bookings.stream().map(b -> {
            List<Ticket> tickets = ticketRepository.findByBooking(b);
            // Lấy danh sách số ghế từ tickets
            List<String> seats = tickets.stream()
                    .map(t -> t.getSeat().getSeatNumber())
                    .collect(Collectors.toList());

            // Xây dựng response object
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

    /**
     * Tìm đơn đặt vé theo mã đặt vé
     * @Transactional(readOnly = true): Chỉ đọc
     * Dùng khi khách hàng nhập mã để tìm booking
     */
    @Override
    @Transactional(readOnly = true)
    public Booking findByBookingCode(String bookingCode) {
        return bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã đơn: " + bookingCode));
    }

    /**
     * Xác nhận đơn đặt vé (nhân viên xác nhận thanh toán)
     * @Transactional: Đảm bảo tính nhất quán
     * Quy trình:
     * 1. Thay đổi tất cả ticket thành ACTIVE
     * 2. Thay đổi booking thành CONFIRMED
     * 3. Ghi lại ai xác nhận và lúc nào (audit trail)
     */
    @Override
    @Transactional
    public void confirmBooking(Long bookingId, Long staffUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Đánh dấu tất cả vé thành ACTIVE (đã xác nhận)
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        for (Ticket t : tickets) {
            t.setStatus(com.cinema.booking.entity.enums.TicketStatus.ACTIVE);
            ticketRepository.save(t);
        }

        // Đánh dấu booking thành CONFIRMED
        booking.setStatus(com.cinema.booking.entity.enums.BookingStatus.CONFIRMED);
        com.cinema.booking.entity.User staff = userRepository.findById(staffUserId)
                .orElse(null);
        if (staff != null) {
            // Ghi lại nhân viên nào xác nhận
            booking.setConfirmedBy(staff);
            booking.setConfirmedAt(java.time.LocalDateTime.now());
        }

        bookingRepository.save(booking);
    }

    /**
     * Hết hạn đơn đặt vé (tự động hủy vì timeout)
     * @Transactional: Đảm bảo tính nhất quán
     * Dùng cho scheduler - tự động hủy đơn PENDING quá lâu
     * (vd: booking hơn 1 tiếng mà khách chưa thanh toán)
     */
    @Override
    @Transactional
    public void expireBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Chỉ hủy những booking còn PENDING
        if (booking.getStatus() != com.cinema.booking.entity.enums.BookingStatus.PENDING) return;

        // Xóa tất cả tickets
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        ticketRepository.deleteAll(tickets);

        // Đánh dấu booking thành CANCELLED
        booking.setStatus(com.cinema.booking.entity.enums.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    /**
     * Lấy tất cả đơn đặt vé đang chờ xác nhận
     * @Transactional(readOnly = true): Chỉ đọc
     * Dùng khi nhân viên xem danh sách booking để xác nhận
     */
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatus(BookingStatus.PENDING);
    }
}
