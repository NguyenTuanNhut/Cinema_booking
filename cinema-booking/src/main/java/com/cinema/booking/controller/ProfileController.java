package com.cinema.booking.controller;

import com.cinema.booking.dto.request.ProfileDto;
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
        ProfileDto dto = new ProfileDto();
        dto.setFullName(profile.getFullName());
        dto.setPhone(profile.getPhone());
        dto.setAddress(profile.getAddress());
        dto.setBirthday(profile.getBirthday());
        dto.setAvatar(profile.getAvatar());

        model.addAttribute("profile", profile);
        model.addAttribute("profileDto", dto);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @org.springframework.validation.annotation.Validated @org.springframework.web.bind.annotation.ModelAttribute("profileDto") ProfileDto profileDto,
                                org.springframework.validation.BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            // re-add profile entity for email display
            Profile profile = profileService.getProfileByEmail(userDetails.getUsername());
            model.addAttribute("profile", profile);
            return "profile/edit";
        }

        profileService.updateProfile(userDetails.getUsername(), profileDto);
        return "redirect:/profile?success";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }
}
