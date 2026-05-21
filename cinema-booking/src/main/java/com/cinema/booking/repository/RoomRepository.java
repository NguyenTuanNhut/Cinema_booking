package com.cinema.booking.repository;

import com.cinema.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý phòng chiếu (Room)
 * Tương tác với bảng Room trong database
 *
 * Mỗi phòng chiếu có:
 * - ID duy nhất
 * - Tên phòng (vd: "Room 1", "Room 2")
 * - Danh sách ghế (Seat)
 * - Danh sách suất chiếu (Showtime)
 */
public interface RoomRepository
        extends JpaRepository<Room, Long> {
    // Sử dụng các phương thức mặc định từ JpaRepository
}