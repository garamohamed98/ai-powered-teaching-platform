package com.mohamedgara.ai_teaching_platform.courses.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record LessonSummaryResponse(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("course_id")
        UUID courseId
) {
}
