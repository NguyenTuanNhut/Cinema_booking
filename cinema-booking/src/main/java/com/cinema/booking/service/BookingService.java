package com.cinema.booking.service;

import com.cinema.booking.dto.request.BookingRequest;
import com.cinema.booking.dto.response.BookingHistoryResponse;
import com.cinema.booking.entity.Booking;

import java.util.List;

/**
 * Service interface quản lý đặt vé
 * Cung cấp các tính năng liên quan đến đặt vé, hủy vé, xác nhận vé
 */
public interface BookingService {
    
    /**
     * Tạo đơn đặt vé mới
     * Quy trình: Kiểm tra showtime & ghế → Tạo booking → Tạo tickets
     * @param userId ID của người đặt
     * @param request Thông tin đặt vé (suất chiếu, danh sách ghế)
     * @return Đối tượng Booking vừa tạo
     * @throws RuntimeException nếu ghế đã được đặt hoặc dữ liệu không hợp lệ
     */
    Booking createBooking(Long userId, BookingRequest request);
    
    /**
     * Hủy đơn đặt vé
     * Chỉ có thể hủy trước 24 giờ chiếu phim
     * @param bookingId ID của đơn đặt vé
     * @throws RuntimeException nếu quá hạn hoặc booking không tồn tại
     */
    void cancelBooking(Long bookingId);
    
    /**
     * Lấy lịch sử đặt vé của người dùng
     * @param userId ID của người dùng
     * @return Danh sách thông tin chi tiết các đơn đặt vé
     */
    List<BookingHistoryResponse> getBookingHistory(Long userId);
    
    /**
     * Tìm đơn đặt vé theo mã đặt vé
     * @param bookingCode Mã đặt vé (vd: "A1B2C3D4")
     * @return Đối tượng Booking
     * @throws RuntimeException nếu booking không tồn tại
     */
    Booking findByBookingCode(String bookingCode);
    
    /**
     * Xác nhận đơn đặt vé (thay đổi trạng thái từ PENDING → CONFIRMED)
     * Dùng khi nhân viên xác nhận đã nhận tiền hoặc xác nhận vé tại rạp
     * @param bookingId ID của đơn đặt vé
     * @param staffUserId ID của nhân viên xác nhận (để ghi audit trail)
     */
    void confirmBooking(Long bookingId, Long staffUserId);
    
    /**
     * Hết hạn đơn đặt vé (tự động hủy booking vì timeout)
     * Dùng cho scheduler - tự động hủy các đơn pending quá lâu
     * @param bookingId ID của đơn đặt vé
     */
    void expireBooking(Long bookingId);
    
    /**
     * Lấy tất cả đơn đặt vé đang chờ xác nhận
     * Dùng hiển thị danh sách cho staff xác nhận
     * @return Danh sách đơn PENDING
     */
    List<Booking> getPendingBookings();
}
