package com.mohamedgara.ai_teaching_platform.exercises.dto;

import java.util.UUID;

public record LessonInfo(
        UUID id,
        String title,
        String content
) {
}
