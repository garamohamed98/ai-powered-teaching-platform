package com.mohamedgara.ai_teaching_platform.courses.dto;

import java.util.UUID;

public record LessonInfo(
        UUID id,
        String title,
        String content
) {
}
