package com.cinema.booking.repository;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Seat;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository quản lý vé xem phim (Ticket)
 * Tương tác với bảng Ticket trong database
 * 
 * Một vé gồm:
 * - Suất chiếu (Showtime)
 * - Ghế ngồi (Seat)
 * - Đơn đặt vé (Booking)
 * - Giá vé
 * - Trạng thái: PENDING (chưa xác nhận), ACTIVE (đã xác nhận), CANCELLED (đã hủy)
 */
public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    /**
     * Kiểm tra xem ghế đã được đặt cho suất chiếu này chưa
     * Dùng để ngăn chặn double booking (đặt cùng một ghế 2 lần)
     * @param showtime Suất chiếu
     * @param seat Ghế
     * @return true nếu ghế đã được đặt, false nếu chưa
     */
    boolean existsByShowtimeAndSeat(
            Showtime showtime,
            Seat seat
    );

    /**
     * Lấy tất cả vé của một đơn đặt vé
     * @param booking Đơn đặt vé
     * @return Danh sách các vé trong đơn này
     */
    List<Ticket> findByBooking(
            Booking booking
    );

    /**
     * Lấy tất cả ghế đã được đặt của một suất chiếu
     * Dùng khi hiển thị sơ đồ ghế (seat map) cho khách hàng xem ghế còn trống
     * @param showtimeId ID của suất chiếu
     * @return Danh sách các vé (ghế đã được đặt)
     */
    List<Ticket> findByShowtimeId(
            Long showtimeId
    );
}