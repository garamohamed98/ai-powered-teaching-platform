package com.mohamedgara.ai_teaching_platform.exercises.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;

import java.util.List;
import java.util.UUID;

public record CreateExerciseResponse (
        UUID id,
        @JsonProperty("lesson_id_list")
        List<UUID> lessonIdList,
        ExerciseType type,
        String title,
        String instructions,
        ExerciseContent content
){
}
