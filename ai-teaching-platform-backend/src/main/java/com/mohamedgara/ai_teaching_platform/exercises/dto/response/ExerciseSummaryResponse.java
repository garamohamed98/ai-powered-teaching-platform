package com.mohamedgara.ai_teaching_platform.exercises.dto.response;

import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;

import java.util.UUID;

public record ExerciseSummaryResponse(
        UUID id,
        String title,
        ExerciseType type,
        LessonSummaryResponse lesson
) {

    public record LessonSummaryResponse(
            UUID id,
            String title
    ) {
    }
}
