package com.cinema.booking.repository;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.User;
import com.cinema.booking.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý đơn đặt vé (Booking)
 * Tương tác với bảng Booking trong database
 *
 * Một đơn đặt vé gồm:
 * - Mã đặt vé duy nhất (bookingCode)
 * - Người đặt (User)
 * - Suất chiếu (Showtime)
 * - Danh sách vé (Ticket)
 * - Trạng thái: PENDING (chưa xác nhận), CONFIRMED (đã xác nhận), CANCELLED (đã hủy)
 */
public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    /**
     * Tìm tất cả đơn đặt vé của một người dùng
     * @param user Đối tượng User
     * @return Danh sách đơn đặt vé
     */
    List<Booking> findByUser(User user);

    /**
     * Tìm đơn đặt vé theo mã đặt vé
     * Mã đặt vé là duy nhất cho mỗi booking
     * @param bookingCode Mã đặt vé
     * @return Optional chứa Booking nếu tìm thấy
     */
    Optional<Booking> findByBookingCode(String bookingCode);

    /**
     * Tìm các đơn đặt vé theo trạng thái và thời gian tạo
     * Dùng cho scheduler tự động hủy các đơn pending quá lâu
     * @param status Trạng thái đơn đặt vé
     * @param before Thời gian mốc
     * @return Danh sách đơn đặt vé
     */
    java.util.List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, java.time.LocalDateTime before);

    /**
     * Tìm tất cả đơn đặt vé theo trạng thái
     * @param status Trạng thái đơn đặt vé
     * @return Danh sách đơn đặt vé
     */
    List<Booking> findByStatus(BookingStatus status);
}