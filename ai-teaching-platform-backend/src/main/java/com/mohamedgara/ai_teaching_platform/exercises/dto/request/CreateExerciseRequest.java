package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JsonDeserialize(using = CreateExerciseRequestDeserializer.class)
public record CreateExerciseRequest(
        @NotNull
        @JsonProperty("lesson_id_list")
        List<UUID> lessonIdList,
        @NotNull
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