package com.mohamedgara.ai_teaching_platform.courses.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record LessonResponse(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,

        @JsonProperty("content")
        String content,

        @JsonProperty("course_id")
        UUID courseId
) {
}
