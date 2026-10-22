package com.mohamedgara.ai_teaching_platform.user.service;

import com.mohamedgara.ai_teaching_platform.user.entity.UserProfile;
import com.mohamedgara.ai_teaching_platform.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfile getOrCreate(Jwt jwt){
        String subject = jwt.getSubject();
        return userProfileRepository.findByAuth0Id(subject).orElseGet(
                ()->create(jwt)
        );
    }

    private UserProfile create(Jwt jwt){
        try {
            return userProfileRepository.save(
                    UserProfile.builder()
                            .auth0Id(jwt.getSubject())
                            .email(jwt.getClaimAsString("https://api.ai-teaching-platform/email"))
                            .name(jwt.getClaimAsString("https://api.ai-teaching-platform/name"))
                            .build()
            );
        }catch (DataIntegrityViolationException e){
            return userProfileRepository.findByAuth0Id(jwt.getSubject()).orElseThrow(
                    ()-> e
            );
        }
    }
}
