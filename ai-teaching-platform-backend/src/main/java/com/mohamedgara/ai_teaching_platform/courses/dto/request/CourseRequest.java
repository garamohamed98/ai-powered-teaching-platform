package com.mohamedgara.ai_teaching_platform.courses.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CourseRequest (
        @NotBlank(message = "course data is required")
        @JsonProperty("course")
        String course
){}
