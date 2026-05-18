package com.cinema.booking.service.impl;

import com.cinema.booking.entity.Profile;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.ProfileRepository;
import com.cinema.booking.repository.UserRepository;
import com.cinema.booking.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public Profile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    @Override
    @Transactional
    public void updateProfile(String email, Profile updatedProfile) {
        Profile profile = getProfileByEmail(email);
        
        profile.setFullName(updatedProfile.getFullName());
        profile.setPhone(updatedProfile.getPhone());
        profile.setAddress(updatedProfile.getAddress());
        profile.setBirthday(updatedProfile.getBirthday());
        profile.setAvatar(updatedProfile.getAvatar());
        
        profileRepository.save(profile);
    }
}
