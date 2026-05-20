package com.cinema.booking.service;

import com.cinema.booking.entity.Profile;
import com.cinema.booking.dto.request.ProfileDto;

public interface ProfileService {
    Profile getProfileByEmail(String email);
    void updateProfile(String email, ProfileDto updatedProfile);
}
