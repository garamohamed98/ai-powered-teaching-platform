package com.mohamedgara.ai_teaching_platform.courses.projection;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record LessonTitleAndContent(
        UUID id,
        String title,
        String content
) {
}
