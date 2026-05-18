package com.cinema.booking.service;

import com.cinema.booking.entity.Profile;

public interface ProfileService {
    Profile getProfileByEmail(String email);
    void updateProfile(String email, Profile updatedProfile);
}
