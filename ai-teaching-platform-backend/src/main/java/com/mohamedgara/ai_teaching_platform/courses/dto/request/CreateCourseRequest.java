package com.mohamedgara.ai_teaching_platform.courses.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCourseRequest(
        @NotBlank(message="title is required")
        @Size(min = 3, max = 50,message = "title should be between 3 and 50 characters")
        @JsonProperty("title")
        String title,

        @NotNull
        @JsonProperty("category_id")
        UUID category_id,

        @NotNull
        @JsonProperty("visibility")
        CourseVisibility visibility


) {
}
