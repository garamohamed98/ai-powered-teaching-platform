package com.mohamedgara.ai_teaching_platform.exercises.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mohamedgara.ai_teaching_platform.exercises.domain.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.domain.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.domain.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.InvalidExerciseContentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExerciseContentMapper {
    private final ObjectMapper objectMapper;

    public ExerciseContent toExerciseContent(JsonNode content, ExerciseType exerciseType){
        try {
            return switch (exerciseType) {
                case MULTIPLE_CHOICE ->
                        objectMapper.convertValue(
                                content,
                                MultipleChoiceContent.class
                        );

                case FILL_IN_BLANK ->
                        objectMapper.convertValue(
                                content,
                                FillInBlankContent.class
                        );
            };
        }catch (IllegalArgumentException e){
            throw new InvalidExerciseContentException();
        }
    }
}
