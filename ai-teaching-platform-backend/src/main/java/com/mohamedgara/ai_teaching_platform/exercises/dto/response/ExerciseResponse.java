package com.mohamedgara.ai_teaching_platform.exercises.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;

import java.util.List;
import java.util.UUID;

public record ExerciseResponse(
        UUID id,
        @JsonProperty("lesson_list")
        List<LessonSummaryResponse> lessonList,
        ExerciseType type,
        String title,
        String instructions,
        ExerciseContent content
) {
        public record LessonSummaryResponse(
                UUID id,
                String title
        ) {
        }
}
