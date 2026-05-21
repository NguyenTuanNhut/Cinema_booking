package com.cinema.booking.service;

import com.cinema.booking.dto.request.RegisterRequest;

/**
 * Service interface quản lý xác thực (Authentication)
 * Cung cấp các tính năng liên quan đến đăng ký người dùng
 */
public interface AuthService {

    /**
     * Đăng ký tài khoản mới cho khách hàng
     * Quy trình: Kiểm tra trùng → Tạo user → Mã hóa password → Tạo profile
     * @param request Thông tin đăng ký (username, email, password, fullName, phone)
     * @throws RuntimeException nếu username hoặc email đã tồn tại
     */
    void register(RegisterRequest request);
}