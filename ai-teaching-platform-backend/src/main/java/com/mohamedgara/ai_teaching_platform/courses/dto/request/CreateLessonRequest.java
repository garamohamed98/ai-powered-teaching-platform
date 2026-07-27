package com.mohamedgara.ai_teaching_platform.courses.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLessonRequest (
        @NotBlank(message="title is required")
        @Size(min = 3, max = 50,message = "title should be between 3 and 50 characters")
        @JsonProperty("title")
        String title
){
}
