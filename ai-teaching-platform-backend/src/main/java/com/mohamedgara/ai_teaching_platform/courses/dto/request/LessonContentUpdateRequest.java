package com.mohamedgara.ai_teaching_platform.courses.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LessonContentUpdateRequest(
        @JsonProperty("content")
        String content
) {
}
