package com.cinema.booking.repository;

import com.cinema.booking.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository quản lý vai trò người dùng (Role)
 * Tương tác với bảng Role trong database
 *
 * Vai trò trong hệ thống:
 * - ROLE_ADMIN: Quản trị viên (quản lý toàn bộ hệ thống)
 * - ROLE_STAFF: Nhân viên rạp (quản lý đặt vé, xác nhận vé)
 * - ROLE_CUSTOMER: Khách hàng (đặt vé, xem lịch sử)
 */
public interface RoleRepository
        extends JpaRepository<Role, Long> {

    /**
     * Tìm vai trò theo tên
     * @param name Tên vai trò (vd: "ROLE_ADMIN")
     * @return Optional chứa Role nếu tìm thấy
     */
    Optional<Role> findByName(String name);
}