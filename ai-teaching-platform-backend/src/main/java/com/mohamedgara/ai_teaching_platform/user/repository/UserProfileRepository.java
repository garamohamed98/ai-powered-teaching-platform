package com.mohamedgara.ai_teaching_platform.user.repository;

import com.mohamedgara.ai_teaching_platform.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByAuth0Id(String auth0Id);
}
