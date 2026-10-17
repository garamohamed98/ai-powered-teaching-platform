package com.mohamedgara.ai_teaching_platform.user.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name
) {
}
