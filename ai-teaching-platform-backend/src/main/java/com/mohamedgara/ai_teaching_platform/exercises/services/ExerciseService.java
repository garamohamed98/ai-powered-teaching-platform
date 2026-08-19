package com.mohamedgara.ai_teaching_platform.exercises.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mohamedgara.ai_teaching_platform.exercises.dto.GeneratedExercise;
import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.courses.service.LessonService;
import com.mohamedgara.ai_teaching_platform.exercises.dto.LessonInfo;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.GenerateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseSummaryResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.ExerciseContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.FillInBlankSentence;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisecontent.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.LessonNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.NoLessonReferenceException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseRequestMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRequestMapper exerciseRequestMapper;
    private final ExerciseResponseMapper exerciseResponseMapper;
    private final ExerciseGeneratorService exerciseGeneratorService;
    private final LessonService lessonService;
    private final ObjectMapper objectMapper;


    public CreateExerciseResponse createExercise(CreateExerciseRequest createExerciseRequest){
        if(!lessonService.lessonListExists(createExerciseRequest.lessonIdList())){
            throw new LessonNotFoundException();
        }
        Exercise exercise = exerciseRequestMapper.toExercise(createExerciseRequest);

        boolean correctAnswers = createExerciseRequest.correctAnswers();

        if(correctAnswers){
            JsonNode result = exerciseGeneratorService.generateExerciseAnswer(exercise.getContent());
            exercise.setContent(result);
        }
        Exercise savedExercise = exerciseRepository.save(exercise);

        return exerciseResponseMapper.toCreateExerciseResponse(savedExercise);
    }

    public List<ExerciseSummaryResponse> getExercises(UUID courseId) {
        Map<UUID,String> lessonsIdAndTitleList = lessonService.getLessonSummaryByCourseId(courseId);
        List<Exercise> exerciseList = exerciseRepository.findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()));

        return exerciseResponseMapper.toExerciseListResponse(exerciseList, lessonsIdAndTitleList);
    }

    public ExerciseResponse getExercise(UUID id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(
                ()-> new ExerciseNotFoundException()
        );
        return exerciseResponseMapper.toExerciseResponse(exercise);
    }

    public void deleteExercise(UUID id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(
                ()-> new ExerciseNotFoundException()
        );
        exerciseRepository.delete(exercise);
    }

    public ExerciseResponse correctExercise(UUID id){
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(
                ()-> new ExerciseNotFoundException()
        );

        JsonNode result = exerciseGeneratorService.generateExerciseAnswer(exercise.getContent());
        exercise.setContent(result);
        Exercise savedExercise = exerciseRepository.save(exercise);

        return exerciseResponseMapper.toExerciseResponse(savedExercise);
    }

    public ExerciseResponse generateExercise(GenerateExerciseRequest generateExerciseRequest) {
        UUID courseId = generateExerciseRequest.courseId();
        List<UUID> lessonIdList = generateExerciseRequest.lessonIdList();
        ExerciseType exerciseType = generateExerciseRequest.type();

        List<LessonInfo> lessonInfoList =
                getLessonInfoList(courseId,lessonIdList);

        if( lessonInfoList.isEmpty() ) throw new NoLessonReferenceException();

        JsonNode lessonReference = buildLessonReference(lessonInfoList);

        JsonNode exerciseExampleReference = buildExerciseExampleReference(exerciseType);

        GeneratedExercise generatedExercise = objectMapper.convertValue(
                exerciseGeneratorService.generateExercise(lessonReference,exerciseExampleReference),
                GeneratedExercise.class
        );

        List<UUID> lessonIdListToSave = lessonInfoList.stream().map(
                lessonInfo -> lessonInfo.id()
        ).toList();

        Exercise exercise = Exercise.builder()
                .lessonIdList(lessonIdListToSave)
                .type(exerciseType)
                .instructions(generatedExercise.instruction())
                .content(generatedExercise.exercise())
                .title(generatedExercise.title())
                .build();

        Exercise savedExercise = exerciseRepository.save(exercise);

        return exerciseResponseMapper.toExerciseResponse(savedExercise);

    }


    private List<LessonInfo> getLessonInfoList(UUID courseId, List<UUID> lessonIdList){
        if(courseId != null){
            return lessonService.getCourseLessonInfoList(courseId).stream().map(
                    lesson -> new LessonInfo(
                            lesson.id(),
                            lesson.title(),
                            lesson.content()
                    )
            ).toList();
        }

        if(!lessonIdList.isEmpty()){
            return  lessonService.getLessonInfoList(lessonIdList).stream().map(
                    lesson -> new LessonInfo(
                            lesson.id(),
                            lesson.title(),
                            lesson.content()
                    )
            ).toList();
        }
        return List.of();
    }

    private JsonNode buildExerciseExampleReference(ExerciseType exerciseType){
        ExerciseContent exerciseContent = switch (exerciseType) {
            case MULTIPLE_CHOICE ->  new MultipleChoiceContent(
                    "Example question here",
                    List.of(
                            "Example option 1",
                            "Example option 2",
                            "Example option 3"
                    ),
                    "Example option 1"
            );

            case FILL_IN_BLANK ->  new FillInBlankContent(
                    List.of(
                            new FillInBlankSentence(
                                    "Example sentence with a ___",
                                    List.of(
                                            "Example answer 1",
                                            "Example answer 2 if applicable"
                                    )
                            ),
                            new FillInBlankSentence(
                                    "Another example sentence with a ___",
                                    List.of(
                                            "Example answer 1",
                                            "Example answer 2 if applicable"
                                    )
                            )
                    )
            );
        };
        return objectMapper.valueToTree(exerciseContent);
    }

    private JsonNode buildLessonReference(List<LessonInfo> lessonTitleAndContentList){
        ObjectNode reference = objectMapper.createObjectNode();

        ArrayNode lessons = reference.putArray("lesson_reference_list");

        lessonTitleAndContentList.forEach(
                lessonTitleAndContent->{
                    lessons.addObject()
                            .put("title",lessonTitleAndContent.title())
                            .put("content",lessonTitleAndContent.content());
                }
        );
        return reference;
    }

}
