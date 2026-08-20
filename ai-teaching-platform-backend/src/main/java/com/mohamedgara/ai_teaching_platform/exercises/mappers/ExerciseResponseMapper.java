package com.mohamedgara.ai_teaching_platform.exercises.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.*;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.ComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent.ExerciseStartContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent.FillInBlankStartContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent.FillInBlankStartSentence;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent.MultipleChoiceStartContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Mapper(componentModel = "spring")
public abstract class ExerciseResponseMapper {
    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(
            target = "content",
            expression = "java(toExerciseContentResponse(exercise.getType(), exercise.getContent()))"
    )
    public abstract CreateExerciseResponse toCreateExerciseResponse(Exercise exercise);

    @Mapping(
            target = "content",
            expression = "java(toExerciseContentResponse(exercise.getType(), exercise.getContent()))"
    )
    public abstract ExerciseResponse toExerciseResponse(Exercise exercise);

    public List<ExerciseSummaryResponse> toExerciseListResponse(
            List<Exercise> exercises,
            Map<UUID, String > lessonTitlesById
    ){
        return exercises.stream()
                .map(exercise -> {
                    List<UUID> lessonIdList = exercise.getLessonIdList();

                    return new ExerciseSummaryResponse(
                            exercise.getId(),
                            exercise.getTitle(),
                            exercise.getType(),
                            lessonIdList.stream().map(
                                    lessonId -> new ExerciseSummaryResponse.LessonSummaryResponse(
                                            lessonId,
                                            lessonTitlesById.get(lessonId)
                                    )
                            ).toList()
                    );
                }).toList();
    };



    protected ExerciseContent toExerciseContentResponse(ExerciseType type, JsonNode content) {
        return switch (type) {
            case MULTIPLE_CHOICE -> objectMapper.convertValue(content, MultipleChoiceContent.class);
            case FILL_IN_BLANK   -> objectMapper.convertValue(content, FillInBlankContent.class);
        };
    }

    @Mapping(target = "id", source = "exercise.id")
    @Mapping(target = "exerciseAttemptId", source = "exerciseAttemptId")
    @Mapping(target = "type", source = "exercise.type")
    @Mapping(target = "title", source = "exercise.title")
    @Mapping(target = "instructions", source = "exercise.instructions")
    @Mapping(
            target = "content",
            expression = "java(toExerciseStartContentResponse(exercise.getType(), exercise.getContent()))"
    )
    public abstract StartExerciseResponse toStartExerciseResponse(Exercise exercise, UUID exerciseAttemptId);

    protected ExerciseStartContent toExerciseStartContentResponse(ExerciseType type, JsonNode content) {
        return switch (type) {
            case MULTIPLE_CHOICE -> new MultipleChoiceStartContent(
                    content.get("question").asText(),
                    objectMapper.convertValue(
                            content.get("options"),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                    )
            );
            case FILL_IN_BLANK   -> {
                FillInBlankContent full = objectMapper.convertValue(content, FillInBlankContent.class);
                yield new FillInBlankStartContent(
                        full.sentences().stream()
                                .map(s -> new FillInBlankStartSentence(s.id(),s.text()))
                                .toList()
                );
            }
        };
    }

    @Mapping(target = "attemptId", source = "exerciseAttemptId")
    @Mapping(target = "exerciseId", source = "exercise.id")
    public abstract SubmitExerciseResponse toSubmitExerciseResponse(
            Exercise exercise,
            UUID exerciseAttemptId,
            Integer score,
            String aiFeedBack,
            Long timeTaken,
            ComparedAnswer comparedAnswer
    );

}
