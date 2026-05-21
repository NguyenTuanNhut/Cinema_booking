package com.cinema.booking.repository;

import com.cinema.booking.entity.Profile;
import com.cinema.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository quản lý hồ sơ người dùng (Profile)
 * Tương tác với bảng Profile trong database
 *
 * Profile chứa thông tin chi tiết của người dùng:
 * - Tên đầy đủ
 * - Số điện thoại
 * - Địa chỉ
 * - Ngày sinh
 * - Avatar
 */
public interface ProfileRepository
        extends JpaRepository<Profile, Long> {

    /**
     * Tìm profile của người dùng
     * Mối quan hệ 1-1 giữa User và Profile
     * @param user Đối tượng User
     * @return Optional chứa Profile nếu tìm thấy
     */
    Optional<Profile> findByUser(User user);
}