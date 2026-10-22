package com.mohamedgara.ai_teaching_platform.user.controller;

import com.mohamedgara.ai_teaching_platform.user.dto.response.UserResponse;
import com.mohamedgara.ai_teaching_platform.user.entity.UserProfile;
import com.mohamedgara.ai_teaching_platform.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserProfileService userProfileService;

    @GetMapping
    public UserResponse user(@AuthenticationPrincipal Jwt jwt){
        UserProfile userProfile = userProfileService.getOrCreate(jwt);

        return new UserResponse(
                userProfile.getId(),
                userProfile.getEmail(),
                userProfile.getName()
        );
    }
}
