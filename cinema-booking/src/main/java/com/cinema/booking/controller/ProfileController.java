package com.cinema.booking.controller;

import com.cinema.booking.entity.Profile;
import com.cinema.booking.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Profile profile = profileService.getProfileByEmail(userDetails.getUsername());
        model.addAttribute("profile", profile);
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Profile profile = profileService.getProfileByEmail(userDetails.getUsername());
        model.addAttribute("profile", profile);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails, 
                                @ModelAttribute Profile profile) {
        profileService.updateProfile(userDetails.getUsername(), profile);
        return "redirect:/profile?success";
    }
}
