package com.cinema.booking.repository;

import com.cinema.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository quản lý người dùng (User)
 * Tương tác với bảng User trong database
 * Cung cấp các phương thức tìm kiếm và kiểm tra user
 */
public interface UserRepository
        extends JpaRepository<User, Long> {

    /**
     * Tìm user theo tên người dùng
     * @param username Tên người dùng
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm user theo email
     * @param email Địa chỉ email
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByEmail(String email);

    /**
     * Kiểm tra tên người dùng đã tồn tại chưa
     * @param username Tên người dùng
     * @return true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra email đã tồn tại chưa
     * @param email Địa chỉ email
     * @return true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByEmail(String email);
}