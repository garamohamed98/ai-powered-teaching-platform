package com.mohamedgara.ai_teaching_platform.exercises.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.ComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;

import java.util.List;
import java.util.UUID;

public record SubmitExerciseResponse(
        @JsonProperty("attempt_id")
        UUID attemptId,
        @JsonProperty("exercise_id")
        UUID exerciseId,
        @JsonProperty("lesson_id_list")
        List<UUID> lessonIdList,
        ExerciseType type,
        String title,
        String instructions,
        @JsonProperty("compared_answer")
        ComparedAnswer comparedAnswer,
        Integer score,
        @JsonProperty("ai_feedback")
        String aiFeedBack,
        @JsonProperty("time_taken")
        Long timeTaken

) {
}
