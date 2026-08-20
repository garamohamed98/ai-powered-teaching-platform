package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.Attempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonDeserialize(using = SubmitExerciseAttemptRequestDeserializer.class)
public record SubmitExerciseAttemptRequest(
        @NotNull
        @JsonProperty("exercise_type")
        ExerciseType exerciseType,
        @NotNull
        @Valid
        Attempt attempt
) {
}
