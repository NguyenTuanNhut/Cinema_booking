package com.cinema.booking.service.impl;

import com.cinema.booking.dto.request.RegisterRequest;
import com.cinema.booking.entity.Profile;
import com.cinema.booking.entity.Role;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.ProfileRepository;
import com.cinema.booking.repository.RoleRepository;
import com.cinema.booking.repository.UserRepository;
import com.cinema.booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Triển khai service xác thực (Authentication)
 * @Service: Đánh dấu đây là service bean của Spring
 * @RequiredArgsConstructor: Tự tạo constructor inject dependencies (final fields)
 */
@Service
public class AuthServiceImpl implements AuthService {

    // Repository để thao tác với User
    private final UserRepository userRepository;

    // Repository để thao tác với Role
    private final RoleRepository roleRepository;

    // Repository để thao tác với Profile
    private final ProfileRepository profileRepository;

    // Dùng để mã hóa password (BCrypt)
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor inject (được tạo bởi @RequiredArgsConstructor)
     */
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                          ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Đăng ký tài khoản mới cho khách hàng
     * Quy trình:
     * 1. Kiểm tra username trùng
     * 2. Kiểm tra email trùng
     * 3. Lấy role mặc định (ROLE_CUSTOMER)
     * 4. Tạo user mới với password mã hóa
     * 5. Tạo profile cho user
     */
    @Override
    public void register(RegisterRequest request) {

        // ================================
        // 1. KIỂM TRA TRÙNG USERNAME
        // ================================
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // ================================
        // 2. KIỂM TRA TRÙNG EMAIL
        // ================================
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // ================================
        // 3. LẤY ROLE MẶC ĐỊNH
        // ================================
        Role role = roleRepository
                .findByName("ROLE_CUSTOMER")
                .orElseThrow(); // nếu không có role thì lỗi

        // ================================
        // 4. TẠO USER MỚI
        // ================================
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Mã hóa password trước khi lưu DB (BCrypt)
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Gán role cho user (ROLE_CUSTOMER)
        user.setRole(role);

        // Lưu user xuống database
        User savedUser = userRepository.save(user);

        // ================================
        // 5. TẠO PROFILE CHO USER
        // ================================
        Profile profile = new Profile();

        // Liên kết 1-1 với user vừa tạo
        profile.setUser(savedUser);

        // Thông tin bổ sung từ register request
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());

        // Lưu profile vào database
        profileRepository.save(profile);
    }
}