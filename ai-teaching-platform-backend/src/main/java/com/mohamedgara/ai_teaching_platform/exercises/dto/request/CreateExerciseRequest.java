package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JsonDeserialize(using = CreateExerciseRequestDeserializer.class)
public record CreateExerciseRequest(
        @NotNull
        UUID courseId,
        @NotNull
        @JsonProperty(value = "type", access = JsonProperty.Access.READ_ONLY)
        ExerciseType type,
        @NotBlank
        String title,
        @NotBlank
        String instructions,
        Boolean correctAnswers,
        @NotNull
        @Valid
        ExerciseContent content
        ){}