package com.mohamedgara.ai_teaching_platform.courses.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CourseContentUpdateRequest (
        @NotBlank(message = "content data is required")
        @JsonProperty("content")
        String content
){}
