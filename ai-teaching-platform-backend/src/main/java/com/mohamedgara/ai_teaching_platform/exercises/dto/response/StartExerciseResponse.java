package com.mohamedgara.ai_teaching_platform.exercises.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent.ExerciseStartContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;


import java.util.UUID;

public record StartExerciseResponse(
        UUID id,
        @JsonProperty("exercise_attempt_id")
        UUID exerciseAttemptId,
        ExerciseType type,
        String title,
        String instructions,
        ExerciseStartContent content
) {
}
