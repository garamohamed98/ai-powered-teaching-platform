package com.mohamedgara.ai_teaching_platform.courses.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseVisibility;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseResponse (
        @JsonProperty("id")
        UUID id,

        @JsonProperty("title")
        String title,

        @JsonProperty("category")
        String category,

        @JsonProperty("visibility")
        CourseVisibility visibility,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonProperty("created_at")
        LocalDateTime createdAt

){
}
