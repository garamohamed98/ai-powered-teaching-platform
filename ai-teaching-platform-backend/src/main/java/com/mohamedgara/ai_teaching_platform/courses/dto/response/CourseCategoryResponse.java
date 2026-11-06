package com.mohamedgara.ai_teaching_platform.courses.dto.response;

import java.util.UUID;

public record CourseCategoryResponse(
        UUID id,
        String name,
        String slug,
        int displayOrder
) {
}
