package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record GenerateExerciseRequest(
        @JsonProperty("lesson_id_list")
        List<UUID> lessonIdList,
        @JsonProperty("course_id")
        UUID courseId,
        @NotNull
        @JsonProperty(value = "type")
        ExerciseType type
) {
}
