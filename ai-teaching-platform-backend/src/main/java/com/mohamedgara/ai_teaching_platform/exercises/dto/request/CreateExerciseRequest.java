package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

import java.util.UUID;

public record CreateExerciseRequest(
        @NotNull
        UUID courseId,

        @NotBlank
        String title,
        @NotBlank
        String instructions,
        @NotNull
        @Valid
        ExerciseContent content
        ){

}
