package com.mohamedgara.ai_teaching_platform.exercises.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;

import java.util.UUID;

public record ExerciseResponse(
        UUID id,
        @JsonProperty("course_id")
        UUID courseId,
        ExerciseType type,
        String title,
        String instructions,
        ExerciseContent content
) {}
