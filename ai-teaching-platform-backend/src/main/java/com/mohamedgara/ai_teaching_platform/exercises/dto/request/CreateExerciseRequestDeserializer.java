package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;


import java.io.IOException;
import java.util.UUID;

public class CreateExerciseRequestDeserializer extends StdDeserializer<CreateExerciseRequest> {
    protected CreateExerciseRequestDeserializer() {
        super(CreateExerciseRequest.class);
    }

    @Override
    public CreateExerciseRequest deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode root = p.readValueAsTree();

        UUID lessonId        = ctx.readTreeAsValue(root.get("lesson_id"), UUID.class);
        ExerciseType type    = ctx.readTreeAsValue(root.get("type"), ExerciseType.class);
        String title         = ctx.readTreeAsValue(root.get("title"), String.class);
        String instructions  = ctx.readTreeAsValue(root.get("instructions"), String.class);
        boolean correctAnswers = ctx.readTreeAsValue(root.get("correct_answers"), Boolean.class);
        JsonNode contentNode = root.get("content");

        ExerciseContent content = switch (type){
            case MULTIPLE_CHOICE -> ctx.readTreeAsValue(contentNode, MultipleChoiceContent.class);
            case FILL_IN_BLANK   -> ctx.readTreeAsValue(contentNode, FillInBlankContent.class);
        };

        return new CreateExerciseRequest(lessonId, type, title, instructions,correctAnswers, content);
    }
}