package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.UUID;

public class CreateExerciseRequestDeserializer extends StdDeserializer<CreateExerciseRequest> {
    protected CreateExerciseRequestDeserializer() {
        super(CreateExerciseRequest.class);
    }

    @Override
    public CreateExerciseRequest deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
        JsonNode root = p.readValueAsTree();

        UUID courseId        = ctx.readTreeAsValue(root.get("course_id"), UUID.class);
        ExerciseType type    = ctx.readTreeAsValue(root.get("type"), ExerciseType.class);
        String title         = ctx.readTreeAsValue(root.get("title"), String.class);
        String instructions  = ctx.readTreeAsValue(root.get("instructions"), String.class);
        JsonNode contentNode = root.get("content");

        ExerciseContent content = switch (type){
            case MULTIPLE_CHOICE -> ctx.readTreeAsValue(contentNode, MultipleChoiceContent.class);
            case FILL_IN_BLANK   -> ctx.readTreeAsValue(contentNode, FillInBlankContent.class);
        };

        return new CreateExerciseRequest(courseId, type, title, instructions, content);
    }
}
