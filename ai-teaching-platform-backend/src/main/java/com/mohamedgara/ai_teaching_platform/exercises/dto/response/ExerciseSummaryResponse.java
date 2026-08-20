package com.mohamedgara.ai_teaching_platform.exercises.dto.response;

import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;

import java.util.List;
import java.util.UUID;

public record ExerciseSummaryResponse(
        UUID id,
        String title,
        ExerciseType type,
        List<LessonSummaryResponse> lesson
) {

    public record LessonSummaryResponse(
            UUID id,
            String title
    ) {
    }
}
