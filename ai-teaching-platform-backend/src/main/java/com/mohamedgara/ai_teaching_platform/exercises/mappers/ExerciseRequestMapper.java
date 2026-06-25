package com.mohamedgara.ai_teaching_platform.exercises.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class ExerciseRequestMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "content",   expression = "java(toJsonNode(createExerciseRequest.content()))")
    public abstract Exercise toExercise(CreateExerciseRequest createExerciseRequest);

    protected JsonNode toJsonNode(ExerciseContent content) {
        return  objectMapper.valueToTree(content);
    }
}
