package com.mohamedgara.ai_teaching_platform.exercises.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.courses.dto.LessonInfo;
import com.mohamedgara.ai_teaching_platform.courses.service.LessonService;
import com.mohamedgara.ai_teaching_platform.AI.dto.GeneratedExercise;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.GenerateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseSummaryResponse;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.LessonNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.NoLessonReferenceException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseRequestMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import com.mohamedgara.ai_teaching_platform.exercises.services.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseRequestMapper exerciseRequestMapper;

    @Mock
    private ExerciseResponseMapper exerciseResponseMapper;

    @Mock
    private ExerciseGeneratorService exerciseGeneratorService;

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private ExerciseService exerciseService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID lessonId;
    private UUID exerciseId;
    private UUID courseId;
    private JsonNode contentJson;
    private Exercise exercise;
    private CreateExerciseRequest createRequest;
    private CreateExerciseRequest createRequestWithAnswers;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        lessonId = UUID.randomUUID();
        exerciseId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("question", "What is 2+2?");
        contentNode.put("correctAnswer", "4");
        contentJson = contentNode;
        exercise = Exercise.builder()
                .id(exerciseId)
                .lessonIdList(List.of(lessonId))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(contentJson)
                .build();
        MultipleChoiceContent content = new MultipleChoiceContent("What is 2+2?", List.of("4", "5"), "4");
        createRequest = new CreateExerciseRequest(
                List.of(lessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer", false, content);
        createRequestWithAnswers = new CreateExerciseRequest(
                List.of(lessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer", true, content);
    }

    @Test
    void createExercise_shouldThrowLessonNotFoundException_whenLessonsDoNotExist() {
        when(lessonService.lessonListExists(List.of(lessonId))).thenReturn(false);

        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class,
                () -> exerciseService.createExercise(createRequest));

        assertEquals("Lesson not Found", exception.getMessage());

        verify(lessonService).lessonListExists(List.of(lessonId));
        verify(exerciseRequestMapper, never()).toExercise(any());
        verify(exerciseGeneratorService, never()).generateExerciseAnswer(any());
        verify(exerciseRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toCreateExerciseResponse(any());
    }

    @Test
    void createExercise_shouldCreateExerciseAndReturnResponse_whenCorrectAnswersIsFalse() {
        CreateExerciseResponse expectedResponse = new CreateExerciseResponse(
                exerciseId, List.of(lessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(lessonService.lessonListExists(List.of(lessonId))).thenReturn(true);
        when(exerciseRequestMapper.toExercise(createRequest)).thenReturn(exercise);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseResponseMapper.toCreateExerciseResponse(exercise)).thenReturn(expectedResponse);

        CreateExerciseResponse response = exerciseService.createExercise(createRequest);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(List.of(lessonId), response.lessonIdList());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());

        verify(lessonService).lessonListExists(List.of(lessonId));
        verify(exerciseRequestMapper).toExercise(createRequest);
        verify(exerciseGeneratorService, never()).generateExerciseAnswer(any());
        verify(exerciseRepository).save(exercise);
        verify(exerciseResponseMapper).toCreateExerciseResponse(exercise);
    }

    @Test
    void createExercise_shouldGenerateAndSetAnswers_whenCorrectAnswersIsTrue() {
        ObjectNode generatedContentNode = objectMapper.createObjectNode();
        generatedContentNode.put("question", "What is 2+2?");
        generatedContentNode.put("correctAnswer", "4");
        JsonNode generatedContent = generatedContentNode;

        CreateExerciseResponse expectedResponse = new CreateExerciseResponse(
                exerciseId, List.of(lessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(lessonService.lessonListExists(List.of(lessonId))).thenReturn(true);
        when(exerciseRequestMapper.toExercise(createRequestWithAnswers)).thenReturn(exercise);
        when(exerciseGeneratorService.generateExerciseAnswer(contentJson)).thenReturn(generatedContent);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseResponseMapper.toCreateExerciseResponse(exercise)).thenReturn(expectedResponse);

        CreateExerciseResponse response = exerciseService.createExercise(createRequestWithAnswers);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(List.of(lessonId), response.lessonIdList());
        assertEquals(generatedContent, exercise.getContent());

        verify(lessonService).lessonListExists(List.of(lessonId));
        verify(exerciseRequestMapper).toExercise(createRequestWithAnswers);
        verify(exerciseGeneratorService).generateExerciseAnswer(contentJson);
        verify(exerciseRepository).save(exercise);
        verify(exerciseResponseMapper).toCreateExerciseResponse(exercise);
    }

    @Test
    void createExercise_shouldCreateExerciseWithMultipleLessonIds_whenAllLessonsExist() {
        UUID secondLessonId = UUID.randomUUID();
        List<UUID> lessonIdList = List.of(lessonId, secondLessonId);
        Exercise multiLessonExercise = Exercise.builder()
                .id(exerciseId)
                .lessonIdList(lessonIdList)
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(contentJson)
                .build();
        MultipleChoiceContent content = new MultipleChoiceContent("What is 2+2?", List.of("4", "5"), "4");
        CreateExerciseRequest multiLessonRequest = new CreateExerciseRequest(
                lessonIdList, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer", false, content);
        CreateExerciseResponse expectedResponse = new CreateExerciseResponse(
                exerciseId, lessonIdList, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(lessonService.lessonListExists(lessonIdList)).thenReturn(true);
        when(exerciseRequestMapper.toExercise(multiLessonRequest)).thenReturn(multiLessonExercise);
        when(exerciseRepository.save(multiLessonExercise)).thenReturn(multiLessonExercise);
        when(exerciseResponseMapper.toCreateExerciseResponse(multiLessonExercise)).thenReturn(expectedResponse);

        CreateExerciseResponse response = exerciseService.createExercise(multiLessonRequest);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(lessonIdList, response.lessonIdList());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());

        verify(lessonService).lessonListExists(lessonIdList);
        verify(exerciseRequestMapper).toExercise(multiLessonRequest);
        verify(exerciseGeneratorService, never()).generateExerciseAnswer(any());
        verify(exerciseRepository).save(multiLessonExercise);
        verify(exerciseResponseMapper).toCreateExerciseResponse(multiLessonExercise);
    }

    @Test
    void getExercises_shouldReturnExerciseListResponse_whenCourseHasLessonsAndExercisesExist() {
        Map<UUID, String> lessonsIdAndTitleList = Map.of(lessonId, "Lesson Title");
        List<Exercise> exerciseList = List.of(exercise);
        ExerciseSummaryResponse expectedResponse = new ExerciseSummaryResponse(
                exerciseId, "Addition", ExerciseType.MULTIPLE_CHOICE,
                List.of(new ExerciseSummaryResponse.LessonSummaryResponse(lessonId, "Lesson Title")));
        List<ExerciseSummaryResponse> expectedList = List.of(expectedResponse);

        when(lessonService.getLessonSummaryByCourseId(courseId)).thenReturn(lessonsIdAndTitleList);
        when(exerciseRepository.findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()))).thenReturn(exerciseList);
        when(exerciseResponseMapper.toExerciseListResponse(exerciseList, lessonsIdAndTitleList)).thenReturn(expectedList);

        List<ExerciseSummaryResponse> result = exerciseService.getExercises(courseId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(exerciseId, result.get(0).id());
        assertEquals("Addition", result.get(0).title());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, result.get(0).type());
        assertEquals(lessonId, result.get(0).lesson().get(0).id());
        assertEquals("Lesson Title", result.get(0).lesson().get(0).title());

        verify(lessonService).getLessonSummaryByCourseId(courseId);
        verify(exerciseRepository).findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()));
        verify(exerciseResponseMapper).toExerciseListResponse(exerciseList, lessonsIdAndTitleList);
    }

    @Test
    void getExercises_shouldReturnEmptyList_whenCourseHasNoLessons() {
        Map<UUID, String> emptyMap = Map.of();
        List<ExerciseSummaryResponse> emptyList = List.of();

        when(lessonService.getLessonSummaryByCourseId(courseId)).thenReturn(emptyMap);
        when(exerciseRepository.findExercises(new ArrayList<>(emptyMap.keySet()))).thenReturn(List.of());
        when(exerciseResponseMapper.toExerciseListResponse(List.of(), emptyMap)).thenReturn(emptyList);

        List<ExerciseSummaryResponse> result = exerciseService.getExercises(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(lessonService).getLessonSummaryByCourseId(courseId);
        verify(exerciseRepository).findExercises(new ArrayList<>(emptyMap.keySet()));
        verify(exerciseResponseMapper).toExerciseListResponse(List.of(), emptyMap);
    }

    @Test
    void getExercises_shouldReturnEmptyList_whenCourseHasLessonsButNoExercises() {
        Map<UUID, String> lessonsIdAndTitleList = Map.of(lessonId, "Lesson Title");
        List<ExerciseSummaryResponse> emptyList = List.of();

        when(lessonService.getLessonSummaryByCourseId(courseId)).thenReturn(lessonsIdAndTitleList);
        when(exerciseRepository.findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()))).thenReturn(List.of());
        when(exerciseResponseMapper.toExerciseListResponse(List.of(), lessonsIdAndTitleList)).thenReturn(emptyList);

        List<ExerciseSummaryResponse> result = exerciseService.getExercises(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(lessonService).getLessonSummaryByCourseId(courseId);
        verify(exerciseRepository).findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()));
        verify(exerciseResponseMapper).toExerciseListResponse(List.of(), lessonsIdAndTitleList);
    }

    @Test
    void getExercises_shouldReturnExerciseListResponseWithMultipleLessons_whenExerciseHasMultipleLessons() {
        UUID secondLessonId = UUID.randomUUID();
        Exercise multiLessonExercise = Exercise.builder()
                .id(exerciseId)
                .lessonIdList(List.of(lessonId, secondLessonId))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .content(contentJson)
                .build();
        Map<UUID, String> lessonsIdAndTitleList = Map.of(
                lessonId, "Lesson Title",
                secondLessonId, "Second Lesson Title");
        List<Exercise> exerciseList = List.of(multiLessonExercise);
        ExerciseSummaryResponse expectedResponse = new ExerciseSummaryResponse(
                exerciseId, "Addition", ExerciseType.MULTIPLE_CHOICE,
                List.of(
                        new ExerciseSummaryResponse.LessonSummaryResponse(lessonId, "Lesson Title"),
                        new ExerciseSummaryResponse.LessonSummaryResponse(secondLessonId, "Second Lesson Title")));
        List<ExerciseSummaryResponse> expectedList = List.of(expectedResponse);

        when(lessonService.getLessonSummaryByCourseId(courseId)).thenReturn(lessonsIdAndTitleList);
        when(exerciseRepository.findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()))).thenReturn(exerciseList);
        when(exerciseResponseMapper.toExerciseListResponse(exerciseList, lessonsIdAndTitleList)).thenReturn(expectedList);

        List<ExerciseSummaryResponse> result = exerciseService.getExercises(courseId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).lesson().size());
        assertEquals(lessonId, result.get(0).lesson().get(0).id());
        assertEquals("Lesson Title", result.get(0).lesson().get(0).title());
        assertEquals(secondLessonId, result.get(0).lesson().get(1).id());
        assertEquals("Second Lesson Title", result.get(0).lesson().get(1).title());

        verify(lessonService).getLessonSummaryByCourseId(courseId);
        verify(exerciseRepository).findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()));
        verify(exerciseResponseMapper).toExerciseListResponse(exerciseList, lessonsIdAndTitleList);
    }

    @Test
    void getExercise_shouldReturnExerciseResponse_whenExerciseExists() {
        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId, List.of(lessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseResponseMapper.toExerciseResponse(exercise)).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.getExercise(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(List.of(lessonId), response.lessonIdList());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseResponseMapper).toExerciseResponse(exercise);
    }

    @Test
    void getExercise_shouldReturnExerciseResponseWithMultipleLessonIds_whenExerciseExists() {
        UUID secondLessonId = UUID.randomUUID();
        Exercise multiLessonExercise = Exercise.builder()
                .id(exerciseId)
                .lessonIdList(List.of(lessonId, secondLessonId))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(contentJson)
                .build();
        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId, List.of(lessonId, secondLessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(multiLessonExercise));
        when(exerciseResponseMapper.toExerciseResponse(multiLessonExercise)).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.getExercise(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(List.of(lessonId, secondLessonId), response.lessonIdList());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseResponseMapper).toExerciseResponse(multiLessonExercise);
    }

    @Test
    void getExercise_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.getExercise(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseResponseMapper, never()).toExerciseResponse(any());
    }

    @Test
    void getExercise_shouldThrowExerciseNotFoundExceptionForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(exerciseRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.getExercise(invalidId));

        verify(exerciseRepository).findById(invalidId);
    }

    @Test
    void deleteExercise_shouldDeleteExercise_whenExerciseExists() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise(exerciseId);

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository).delete(exercise);
    }

    @Test
    void deleteExercise_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.deleteExercise(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository, never()).delete(any());
    }

    @Test
    void deleteExercise_shouldThrowExerciseNotFoundExceptionForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(exerciseRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.deleteExercise(invalidId));

        verify(exerciseRepository).findById(invalidId);
    }

    @Test
    void correctExercise_shouldGenerateAnswersSetContentAndReturnResponse_whenExerciseExists() {
        ObjectNode generatedContentNode = objectMapper.createObjectNode();
        generatedContentNode.put("question", "What is 2+2?");
        generatedContentNode.put("correctAnswer", "4");
        JsonNode generatedContent = generatedContentNode;

        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId, List.of(lessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseGeneratorService.generateExerciseAnswer(contentJson)).thenReturn(generatedContent);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseResponseMapper.toExerciseResponse(exercise)).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.correctExercise(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(List.of(lessonId), response.lessonIdList());
        assertEquals(generatedContent, exercise.getContent());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseGeneratorService).generateExerciseAnswer(contentJson);
        verify(exerciseRepository).save(exercise);
        verify(exerciseResponseMapper).toExerciseResponse(exercise);
    }

    @Test
    void correctExercise_shouldReturnExerciseResponseWithMultipleLessonIds_whenExerciseExists() {
        UUID secondLessonId = UUID.randomUUID();
        Exercise multiLessonExercise = Exercise.builder()
                .id(exerciseId)
                .lessonIdList(List.of(lessonId, secondLessonId))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(contentJson)
                .build();
        ObjectNode generatedContentNode = objectMapper.createObjectNode();
        generatedContentNode.put("question", "What is 2+2?");
        generatedContentNode.put("correctAnswer", "4");
        JsonNode generatedContent = generatedContentNode;

        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId, List.of(lessonId, secondLessonId), ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(multiLessonExercise));
        when(exerciseGeneratorService.generateExerciseAnswer(contentJson)).thenReturn(generatedContent);
        when(exerciseRepository.save(multiLessonExercise)).thenReturn(multiLessonExercise);
        when(exerciseResponseMapper.toExerciseResponse(multiLessonExercise)).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.correctExercise(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(List.of(lessonId, secondLessonId), response.lessonIdList());
        assertEquals(generatedContent, multiLessonExercise.getContent());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseGeneratorService).generateExerciseAnswer(contentJson);
        verify(exerciseRepository).save(multiLessonExercise);
        verify(exerciseResponseMapper).toExerciseResponse(multiLessonExercise);
    }

    @Test
    void correctExercise_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.correctExercise(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseGeneratorService, never()).generateExerciseAnswer(any());
        verify(exerciseRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toExerciseResponse(any());
    }

    @Test
    void correctExercise_shouldThrowExerciseNotFoundExceptionForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(exerciseRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.correctExercise(invalidId));

        verify(exerciseRepository).findById(invalidId);
    }

    @Test
    void generateExercise_shouldBuildAndReturnExerciseResponse_whenCourseIdIsProvided() {
        GenerateExerciseRequest request = new GenerateExerciseRequest(
                List.of(lessonId),
                courseId,
                ExerciseType.MULTIPLE_CHOICE
        );
        LessonInfo lessonInfo = new LessonInfo(lessonId, "Lesson One", "Content One");
        ObjectNode exerciseNode = objectMapper.createObjectNode();
        exerciseNode.put("question", "What is 2+2?");
        GeneratedExercise generatedExercise = new GeneratedExercise(
                "Generated Title",
                "Generated instructions",
                exerciseNode
        );
        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId,
                List.of(lessonId),
                ExerciseType.MULTIPLE_CHOICE,
                "Generated Title",
                "Generated instructions",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4"), "4"));

        when(lessonService.getCourseLessonInfoList(courseId))
                .thenReturn(Collections.singletonList(lessonInfo));
        when(exerciseGeneratorService.generateExercise(any(JsonNode.class), any(JsonNode.class)))
                .thenReturn(generatedExercise);
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(exerciseResponseMapper.toExerciseResponse(any(Exercise.class))).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.generateExercise(request);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(List.of(lessonId), response.lessonIdList());
        assertEquals("Generated Title", response.title());
        assertEquals("Generated instructions", response.instructions());

        verify(lessonService).getCourseLessonInfoList(courseId);
        verify(exerciseGeneratorService).generateExercise(any(JsonNode.class), any(JsonNode.class));
        verify(exerciseRepository).save(any(Exercise.class));
        verify(exerciseResponseMapper).toExerciseResponse(any(Exercise.class));
    }

    @Test
    void generateExercise_shouldBuildAndReturnExerciseResponse_whenCourseIdIsNullAndLessonIdsAreProvided() {
        List<UUID> lessonIdList = List.of(lessonId);
        GenerateExerciseRequest request = new GenerateExerciseRequest(
                lessonIdList,
                null,
                ExerciseType.MULTIPLE_CHOICE
        );
        LessonInfo lessonInfo = new LessonInfo(lessonId, "Lesson One", "Content One");
        ObjectNode exerciseNode = objectMapper.createObjectNode();
        exerciseNode.put("question", "What is 2+2?");
        GeneratedExercise generatedExercise = new GeneratedExercise(
                "Generated Title",
                "Generated instructions",
                exerciseNode
        );
        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId,
                lessonIdList,
                ExerciseType.MULTIPLE_CHOICE,
                "Generated Title",
                "Generated instructions",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4"), "4"));

        when(lessonService.getLessonInfoList(lessonIdList))
                .thenReturn(Collections.singletonList(lessonInfo));
        when(exerciseGeneratorService.generateExercise(any(JsonNode.class), any(JsonNode.class)))
                .thenReturn(generatedExercise);
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(exerciseResponseMapper.toExerciseResponse(any(Exercise.class))).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.generateExercise(request);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(lessonIdList, response.lessonIdList());
        assertEquals("Generated Title", response.title());
        assertEquals("Generated instructions", response.instructions());

        verify(lessonService).getLessonInfoList(lessonIdList);
        verify(exerciseGeneratorService).generateExercise(any(JsonNode.class), any(JsonNode.class));
        verify(exerciseRepository).save(any(Exercise.class));
        verify(exerciseResponseMapper).toExerciseResponse(any(Exercise.class));
    }

    @Test
    void generateExercise_shouldThrowNoLessonReferenceException_whenCourseIdIsProvidedButNoLessonInfoExists() {
        GenerateExerciseRequest request = new GenerateExerciseRequest(
                List.of(lessonId),
                courseId,
                ExerciseType.MULTIPLE_CHOICE
        );

        when(lessonService.getCourseLessonInfoList(courseId)).thenReturn(Collections.emptyList());

        assertThrows(NoLessonReferenceException.class,
                () -> exerciseService.generateExercise(request));

        verify(exerciseGeneratorService, never()).generateExercise(any(JsonNode.class), any(JsonNode.class));
        verify(exerciseRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toExerciseResponse(any());
    }
}
