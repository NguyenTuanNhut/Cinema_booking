package com.cinema.booking.service;

import com.cinema.booking.dto.request.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);
}