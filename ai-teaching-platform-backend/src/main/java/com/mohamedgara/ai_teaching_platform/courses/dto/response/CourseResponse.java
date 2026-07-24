package com.mohamedgara.ai_teaching_platform.courses.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CourseResponse (
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title
){
}
