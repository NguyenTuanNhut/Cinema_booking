package com.cinema.booking.service.impl;

import com.cinema.booking.dto.request.ProfileDto;
import com.cinema.booking.entity.Profile;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.ProfileRepository;
import com.cinema.booking.repository.UserRepository;
import com.cinema.booking.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Triển khai service quản lý hồ sơ người dùng
 * @Service: Đánh dấu đây là service bean của Spring
 * @RequiredArgsConstructor: Tự tạo constructor inject dependencies
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    // Repository để thao tác với Profile
    private final ProfileRepository profileRepository;
    // Repository để thao tác với User
    private final UserRepository userRepository;

    /**
     * Lấy thông tin profile của người dùng theo email
     * Quy trình:
     * 1. Tìm user theo email
     * 2. Lấy profile của user đó
     */
    @Override
    public Profile getProfileByEmail(String email) {
        // Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Lấy profile của user
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    /**
     * Cập nhật thông tin profile người dùng
     * @Transactional: Đảm bảo tính nhất quán dữ liệu
     * Cập nhật các trường: Tên, SĐT, Địa chỉ, Ngày sinh, Avatar
     */
    @Override
    @Transactional
    public void updateProfile(String email, ProfileDto updatedProfile) {
        // Lấy profile hiện tại
        Profile profile = getProfileByEmail(email);

        // Cập nhật các trường
        profile.setFullName(updatedProfile.getFullName());
        profile.setPhone(updatedProfile.getPhone());
        profile.setAddress(updatedProfile.getAddress());
        profile.setBirthday(updatedProfile.getBirthday());
        profile.setAvatar(updatedProfile.getAvatar());

        // Lưu lại vào database
        profileRepository.save(profile);
    }
}
