package com.mohamedgara.ai_teaching_platform.exercises.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.Attempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.FillInBlankAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.MultipleChoiceAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.InvalidAttemptTypeException;

import java.io.IOException;

public class SubmitExerciseAttemptRequestDeserializer extends StdDeserializer<SubmitExerciseAttemptRequest> {
    protected SubmitExerciseAttemptRequestDeserializer(){
        super(SubmitExerciseAttemptRequest.class);
    }

    @Override
    public SubmitExerciseAttemptRequest deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode root = p.readValueAsTree();

        ExerciseType exerciseType = ctx.readTreeAsValue(root.get("exercise_type"), ExerciseType.class);
        JsonNode attemptNode = root.get("attempt");

        try{
            Attempt attempt = switch (exerciseType){
                case MULTIPLE_CHOICE -> ctx.readTreeAsValue(attemptNode, MultipleChoiceAttempt.class);
                case FILL_IN_BLANK -> ctx.readTreeAsValue(attemptNode, FillInBlankAttempt.class);
            };
            return new SubmitExerciseAttemptRequest(exerciseType,attempt);
        }catch (JsonProcessingException e){
            throw new InvalidAttemptTypeException();
        }
    }


}
