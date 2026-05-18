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

@Service // đánh dấu đây là service bean
@RequiredArgsConstructor // tự tạo constructor inject dependency
public class AuthServiceImpl implements AuthService {

    // repository thao tác bảng user
    private final UserRepository userRepository;

    // repository thao tác bảng role
    private final RoleRepository roleRepository;

    // repository thao tác bảng profile
    private final ProfileRepository profileRepository;

    // dùng để mã hóa password (BCrypt)
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {

        // ================================
        // 1. CHECK TRÙNG USERNAME
        // ================================
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // ================================
        // 2. CHECK TRÙNG EMAIL
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

        // mã hóa password trước khi lưu DB
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // gán role cho user
        user.setRole(role);

        // lưu user xuống database
        User savedUser = userRepository.save(user);

        // ================================
        // 5. TẠO PROFILE CHO USER
        // ================================
        Profile profile = new Profile();

        // liên kết 1-1 với user vừa tạo
        profile.setUser(savedUser);

        // thông tin bổ sung
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());

        // lưu profile
        profileRepository.save(profile);
    }
}