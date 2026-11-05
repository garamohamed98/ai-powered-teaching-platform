package com.mohamedgara.ai_teaching_platform.courses.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateCourseRequest(
        @Size(min = 3, max = 50,message = "title should be between 3 and 50 characters")
        @JsonProperty("title")
        String title,

        @JsonProperty("category_id")
        UUID categoryId,
        @JsonProperty("visibility")
        CourseVisibility visibility
){}
