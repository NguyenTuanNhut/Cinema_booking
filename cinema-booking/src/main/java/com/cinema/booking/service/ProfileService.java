package com.cinema.booking.service;

import com.cinema.booking.entity.Profile;
import com.cinema.booking.dto.request.ProfileDto;

/**
 * Service interface quản lý hồ sơ người dùng
 * Cung cấp các tính năng xem và cập nhật thông tin profile
 */
public interface ProfileService {

    /**
     * Lấy thông tin profile của người dùng
     * @param email Email của người dùng
     * @return Đối tượng Profile
     * @throws RuntimeException nếu user hoặc profile không tồn tại
     */
    Profile getProfileByEmail(String email);

    /**
     * Cập nhật thông tin profile người dùng
     * Cập nhật: tên đầy đủ, số điện thoại, địa chỉ, ngày sinh, avatar
     * @param email Email của người dùng
     * @param updatedProfile Thông tin profile mới
     */
    void updateProfile(String email, ProfileDto updatedProfile);
}
