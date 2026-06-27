package com.mohamedgara.ai_teaching_platform.exercises.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class ExerciseResponseMapper {
    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(
            target = "content",
            expression = "java(toContentResponse(exercise.getType(), exercise.getContent()))"
    )
    public abstract CreateExerciseResponse toCreateExerciseResponse(Exercise exercise);

    @Mapping(
            target = "content",
            expression = "java(toContentResponse(exercise.getType(), exercise.getContent()))"
    )
    public abstract ExerciseResponse toExerciseResponse(Exercise exercise);

    public abstract List<ExerciseResponse> toExerciseListResponse(List<Exercise> exercises);

    protected ExerciseContent toContentResponse(ExerciseType type, JsonNode content) {
        return switch (type) {
            case MULTIPLE_CHOICE -> objectMapper.convertValue(content, MultipleChoiceContent.class);
            case FILL_IN_BLANK   -> objectMapper.convertValue(content, FillInBlankContent.class);
        };
    }
}
