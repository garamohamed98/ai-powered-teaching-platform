package com.mohamedgara.ai_teaching_platform.exercises.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.courses.dto.LessonInfo;
import com.mohamedgara.ai_teaching_platform.courses.service.LessonService;
import com.mohamedgara.ai_teaching_platform.exercises.domain.FillInBlankContent;
import com.mohamedgara.ai_teaching_platform.exercises.domain.FillInBlankSentence;
import com.mohamedgara.ai_teaching_platform.exercises.domain.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.SubmitExerciseAttemptRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.FillInBlankAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.FillInBlankSentenceAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt.MultipleChoiceAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.StartExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.SubmitExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.FillInBlankComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.FillInBlankSentenceComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer.MultipleChoiceComparedAnswer;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent.MultipleChoiceStartContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseAttemptNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.InvalidAttemptTypeException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseContentMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseAttemptRepository;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import com.mohamedgara.ai_teaching_platform.exercises.services.ExerciseAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseAttemptServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseResponseMapper exerciseResponseMapper;

    @Mock
    private ExerciseAttemptRepository exerciseAttemptRepository;

    @Mock
    private ExerciseContentMapper exerciseContentMapper;

    @Mock
    private LessonService lessonService;

    @Mock
    private ExerciseGeneratorService exerciseGeneratorService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ExerciseAttemptService exerciseAttemptService;

    private UUID exerciseId;
    private UUID exerciseAttemptId;
    private UUID sentenceId;
    private JsonNode contentJson;
    private Exercise exercise;
    private ExerciseAttempt exerciseAttempt;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        exerciseId = UUID.randomUUID();
        exerciseAttemptId = UUID.randomUUID();
        sentenceId = UUID.randomUUID();
        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("question", "What is 2+2?");
        contentNode.put("correctAnswer", "4");
        contentJson = contentNode;
        exercise = Exercise.builder()
                .id(exerciseId)
                .lessonIdList(List.of(UUID.randomUUID()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(contentJson)
                .build();
        exerciseAttempt = ExerciseAttempt.builder()
                .id(exerciseAttemptId)
                .exercise(exercise)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void startExerciseAttempt_shouldCreateAttemptAndReturnResponse_whenExerciseExists() {
        StartExerciseResponse expectedResponse = new StartExerciseResponse(
                exerciseId, exerciseAttemptId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new MultipleChoiceStartContent("What is 2+2?", List.of("4", "5")));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseAttemptRepository.save(any(ExerciseAttempt.class))).thenAnswer(invocation -> {
            ExerciseAttempt attempt = invocation.getArgument(0);
            assertSame(exercise, attempt.getExercise());
            attempt.setId(exerciseAttemptId);
            return attempt;
        });
        when(exerciseResponseMapper.toStartExerciseResponse(exercise, exerciseAttemptId)).thenReturn(expectedResponse);

        StartExerciseResponse response = exerciseAttemptService.startExerciseAttempt(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(exerciseAttemptId, response.exerciseAttemptId());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseAttemptRepository).save(any(ExerciseAttempt.class));
        verify(exerciseResponseMapper).toStartExerciseResponse(exercise, exerciseAttemptId);
    }

    @Test
    void startExerciseAttempt_shouldSaveAttemptWithExercise() {
        StartExerciseResponse expectedResponse = new StartExerciseResponse(
                exerciseId, exerciseAttemptId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new MultipleChoiceStartContent("What is 2+2?", List.of("4", "5")));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseAttemptRepository.save(any(ExerciseAttempt.class))).thenAnswer(invocation -> {
            ExerciseAttempt attempt = invocation.getArgument(0);
            assertEquals(exercise.getId(), attempt.getExercise().getId());
            attempt.setId(exerciseAttemptId);
            return attempt;
        });
        when(exerciseResponseMapper.toStartExerciseResponse(exercise, exerciseAttemptId)).thenReturn(expectedResponse);

        exerciseAttemptService.startExerciseAttempt(exerciseId);

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseAttemptRepository).save(any(ExerciseAttempt.class));
        verify(exerciseResponseMapper).toStartExerciseResponse(exercise, exerciseAttemptId);
    }


    @Test
    void startExerciseAttempt_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseAttemptService.startExerciseAttempt(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseAttemptRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toStartExerciseResponse(any(), any());
    }

    @Test
    void submitExerciseAttempt_shouldSubmitMultipleChoiceAndReturnResponse_whenAnswerIsCorrect() {
        MultipleChoiceContent multipleChoiceContent = new MultipleChoiceContent("What is 2+2?", List.of("4", "5"), "4");
        MultipleChoiceAttempt attempt = new MultipleChoiceAttempt("4");
        SubmitExerciseAttemptRequest request = new SubmitExerciseAttemptRequest(ExerciseType.MULTIPLE_CHOICE, attempt);
        MultipleChoiceComparedAnswer comparedAnswer = new MultipleChoiceComparedAnswer(
                "What is 2+2?", List.of("4", "5"), "4", "4", true);
        SubmitExerciseResponse expectedResponse = new SubmitExerciseResponse(
                exerciseAttemptId, exerciseId, exercise.getLessonIdList(), ExerciseType.MULTIPLE_CHOICE,
                "Addition", "Choose the correct answer", comparedAnswer, 10, "Good job!", 0L);

        when(exerciseAttemptRepository.findById(exerciseAttemptId)).thenReturn(Optional.of(exerciseAttempt));
        when(exerciseContentMapper.toExerciseContent(any(JsonNode.class), eq(ExerciseType.MULTIPLE_CHOICE)))
                .thenReturn(multipleChoiceContent);
        when(lessonService.getLessonInfoList(any()))
                .thenReturn(List.of(new LessonInfo(UUID.randomUUID(), "Lesson One", "Content One")));
        when(exerciseGeneratorService.generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class)))
                .thenReturn("Good job!");
        when(exerciseAttemptRepository.save(any(ExerciseAttempt.class))).thenAnswer(invocation -> {
            ExerciseAttempt savedAttempt = invocation.getArgument(0);
            assertEquals(10, savedAttempt.getScore());
            assertEquals("Good job!", savedAttempt.getAiFeedback());
            assertNotNull(savedAttempt.getSubmittedAt());
            return savedAttempt;
        });
        when(exerciseResponseMapper.toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any())).thenReturn(expectedResponse);

        SubmitExerciseResponse response = exerciseAttemptService.submitExerciseAttempt(exerciseAttemptId, request);

        assertNotNull(response);
        assertEquals(exerciseAttemptId, response.attemptId());
        assertEquals(exerciseId, response.exerciseId());
        assertEquals(exercise.getLessonIdList(), response.lessonIdList());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());
        assertEquals(comparedAnswer, response.comparedAnswer());
        assertEquals(10, response.score());
        assertEquals("Good job!", response.aiFeedBack());

        verify(exerciseAttemptRepository).findById(exerciseAttemptId);
        verify(exerciseContentMapper).toExerciseContent(any(JsonNode.class), eq(ExerciseType.MULTIPLE_CHOICE));
        verify(lessonService).getLessonInfoList(any());
        verify(exerciseGeneratorService).generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class));
        verify(exerciseAttemptRepository).save(any(ExerciseAttempt.class));
        verify(exerciseResponseMapper).toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any());
    }

    @Test
    void submitExerciseAttempt_shouldSubmitMultipleChoiceWithScoreZero_whenAnswerIsIncorrect() {
        MultipleChoiceContent multipleChoiceContent = new MultipleChoiceContent("What is 2+2?", List.of("4", "5"), "4");
        MultipleChoiceAttempt attempt = new MultipleChoiceAttempt("5");
        SubmitExerciseAttemptRequest request = new SubmitExerciseAttemptRequest(ExerciseType.MULTIPLE_CHOICE, attempt);
        MultipleChoiceComparedAnswer comparedAnswer = new MultipleChoiceComparedAnswer(
                "What is 2+2?", List.of("4", "5"), "4", "5", false);
        SubmitExerciseResponse expectedResponse = new SubmitExerciseResponse(
                exerciseAttemptId, exerciseId, exercise.getLessonIdList(), ExerciseType.MULTIPLE_CHOICE,
                "Addition", "Choose the correct answer", comparedAnswer, 0, "Try again!", 0L);

        when(exerciseAttemptRepository.findById(exerciseAttemptId)).thenReturn(Optional.of(exerciseAttempt));
        when(exerciseContentMapper.toExerciseContent(any(JsonNode.class), eq(ExerciseType.MULTIPLE_CHOICE)))
                .thenReturn(multipleChoiceContent);
        when(lessonService.getLessonInfoList(any()))
                .thenReturn(List.of(new LessonInfo(UUID.randomUUID(), "Lesson One", "Content One")));
        when(exerciseGeneratorService.generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class)))
                .thenReturn("Try again!");
        when(exerciseAttemptRepository.save(any(ExerciseAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(exerciseResponseMapper.toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any())).thenReturn(expectedResponse);

        SubmitExerciseResponse response = exerciseAttemptService.submitExerciseAttempt(exerciseAttemptId, request);

        assertNotNull(response);
        assertEquals(comparedAnswer, response.comparedAnswer());
        assertEquals(0, response.score());
        assertEquals("Try again!", response.aiFeedBack());

        verify(exerciseAttemptRepository).findById(exerciseAttemptId);
        verify(exerciseContentMapper).toExerciseContent(any(JsonNode.class), eq(ExerciseType.MULTIPLE_CHOICE));
        verify(lessonService).getLessonInfoList(any());
        verify(exerciseGeneratorService).generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class));
        verify(exerciseAttemptRepository).save(any(ExerciseAttempt.class));
        verify(exerciseResponseMapper).toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any());
    }

    @Test
    void submitExerciseAttempt_shouldSubmitFillInBlankAndReturnResponse_whenAllAnswersCorrect() {
        UUID fillInBlankExerciseId = UUID.randomUUID();
        UUID fillInBlankAttemptId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        Exercise fillInBlankExercise = Exercise.builder()
                .id(fillInBlankExerciseId)
                .lessonIdList(List.of(lessonId))
                .type(ExerciseType.FILL_IN_BLANK)
                .title("Fill the blanks")
                .instructions("Complete each sentence")
                .content(objectMapper.valueToTree(Map.of("sentences", List.of(
                        Map.of("id", sentenceId.toString(), "text", "2 plus 2 is ____", "answers", List.of("4"))))))
                .build();
        ExerciseAttempt fillInBlankAttempt = ExerciseAttempt.builder()
                .id(fillInBlankAttemptId)
                .exercise(fillInBlankExercise)
                .createdAt(LocalDateTime.now())
                .build();
        FillInBlankContent fillInBlankContent = new FillInBlankContent(List.of(
                new FillInBlankSentence(sentenceId, "2 plus 2 is ____", List.of("4"))));
        FillInBlankAttempt attempt = new FillInBlankAttempt(List.of(
                new FillInBlankSentenceAttempt(sentenceId, "4")));
        SubmitExerciseAttemptRequest request = new SubmitExerciseAttemptRequest(ExerciseType.FILL_IN_BLANK, attempt);
        FillInBlankComparedAnswer comparedAnswer = new FillInBlankComparedAnswer(List.of(
                new FillInBlankSentenceComparedAnswer("2 plus 2 is ____", List.of("4"), "4", true)));
        SubmitExerciseResponse expectedResponse = new SubmitExerciseResponse(
                fillInBlankAttemptId, fillInBlankExerciseId, fillInBlankExercise.getLessonIdList(),
                ExerciseType.FILL_IN_BLANK, "Fill the blanks", "Complete each sentence",
                comparedAnswer, 10, "Good job!", 0L);

        when(exerciseAttemptRepository.findById(fillInBlankAttemptId)).thenReturn(Optional.of(fillInBlankAttempt));
        when(exerciseContentMapper.toExerciseContent(any(JsonNode.class), eq(ExerciseType.FILL_IN_BLANK)))
                .thenReturn(fillInBlankContent);
        when(lessonService.getLessonInfoList(any()))
                .thenReturn(List.of(new LessonInfo(lessonId, "Lesson One", "Content One")));
        when(exerciseGeneratorService.generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class)))
                .thenReturn("Good job!");
        when(exerciseAttemptRepository.save(any(ExerciseAttempt.class))).thenAnswer(invocation -> {
            ExerciseAttempt savedAttempt = invocation.getArgument(0);
            assertEquals(10, savedAttempt.getScore());
            assertEquals("Good job!", savedAttempt.getAiFeedback());
            assertNotNull(savedAttempt.getSubmittedAt());
            return savedAttempt;
        });
        when(exerciseResponseMapper.toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any())).thenReturn(expectedResponse);

        SubmitExerciseResponse response = exerciseAttemptService.submitExerciseAttempt(fillInBlankAttemptId, request);

        assertNotNull(response);
        assertEquals(fillInBlankAttemptId, response.attemptId());
        assertEquals(fillInBlankExerciseId, response.exerciseId());
        assertEquals(fillInBlankExercise.getLessonIdList(), response.lessonIdList());
        assertEquals(ExerciseType.FILL_IN_BLANK, response.type());
        assertEquals("Fill the blanks", response.title());
        assertEquals("Complete each sentence", response.instructions());
        assertEquals(comparedAnswer, response.comparedAnswer());
        assertEquals(10, response.score());
        assertEquals("Good job!", response.aiFeedBack());

        verify(exerciseAttemptRepository).findById(fillInBlankAttemptId);
        verify(exerciseContentMapper).toExerciseContent(any(JsonNode.class), eq(ExerciseType.FILL_IN_BLANK));
        verify(lessonService).getLessonInfoList(any());
        verify(exerciseGeneratorService).generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class));
        verify(exerciseAttemptRepository).save(any(ExerciseAttempt.class));
        verify(exerciseResponseMapper).toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any());
    }

    @Test
    void submitExerciseAttempt_shouldSubmitFillInBlankWithPartialScore_whenSomeAnswersIncorrect() {
        UUID secondSentenceId = UUID.randomUUID();
        UUID fillInBlankExerciseId = UUID.randomUUID();
        UUID fillInBlankAttemptId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        Exercise fillInBlankExercise = Exercise.builder()
                .id(fillInBlankExerciseId)
                .lessonIdList(List.of(lessonId))
                .type(ExerciseType.FILL_IN_BLANK)
                .title("Fill the blanks")
                .instructions("Complete each sentence")
                .content(objectMapper.valueToTree(Map.of("sentences", List.of(
                        Map.of("id", sentenceId.toString(), "text", "2 plus 2 is ____", "answers", List.of("4")),
                        Map.of("id", secondSentenceId.toString(), "text", "2 times 2 is ____", "answers", List.of("4"))))))
                .build();
        ExerciseAttempt fillInBlankAttempt = ExerciseAttempt.builder()
                .id(fillInBlankAttemptId)
                .exercise(fillInBlankExercise)
                .createdAt(LocalDateTime.now())
                .build();
        FillInBlankContent fillInBlankContent = new FillInBlankContent(List.of(
                new FillInBlankSentence(sentenceId, "2 plus 2 is ____", List.of("4")),
                new FillInBlankSentence(secondSentenceId, "2 times 2 is ____", List.of("4"))));
        FillInBlankAttempt attempt = new FillInBlankAttempt(List.of(
                new FillInBlankSentenceAttempt(sentenceId, "4"),
                new FillInBlankSentenceAttempt(secondSentenceId, "5")));
        SubmitExerciseAttemptRequest request = new SubmitExerciseAttemptRequest(ExerciseType.FILL_IN_BLANK, attempt);
        FillInBlankComparedAnswer comparedAnswer = new FillInBlankComparedAnswer(List.of(
                new FillInBlankSentenceComparedAnswer("2 plus 2 is ____", List.of("4"), "4", true),
                new FillInBlankSentenceComparedAnswer("2 times 2 is ____", List.of("4"), "5", false)));
        SubmitExerciseResponse expectedResponse = new SubmitExerciseResponse(
                fillInBlankAttemptId, fillInBlankExerciseId, fillInBlankExercise.getLessonIdList(),
                ExerciseType.FILL_IN_BLANK, "Fill the blanks", "Complete each sentence",
                comparedAnswer, 5, "Partial credit", 0L);

        when(exerciseAttemptRepository.findById(fillInBlankAttemptId)).thenReturn(Optional.of(fillInBlankAttempt));
        when(exerciseContentMapper.toExerciseContent(any(JsonNode.class), eq(ExerciseType.FILL_IN_BLANK)))
                .thenReturn(fillInBlankContent);
        when(lessonService.getLessonInfoList(any()))
                .thenReturn(List.of(new LessonInfo(lessonId, "Lesson One", "Content One")));
        when(exerciseGeneratorService.generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class)))
                .thenReturn("Partial credit");
        when(exerciseAttemptRepository.save(any(ExerciseAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(exerciseResponseMapper.toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any())).thenReturn(expectedResponse);

        SubmitExerciseResponse response = exerciseAttemptService.submitExerciseAttempt(fillInBlankAttemptId, request);

        assertNotNull(response);
        assertEquals(comparedAnswer, response.comparedAnswer());
        assertEquals(5, response.score());
        assertEquals("Partial credit", response.aiFeedBack());

        verify(exerciseAttemptRepository).findById(fillInBlankAttemptId);
        verify(exerciseContentMapper).toExerciseContent(any(JsonNode.class), eq(ExerciseType.FILL_IN_BLANK));
        verify(lessonService).getLessonInfoList(any());
        verify(exerciseGeneratorService).generateAttemptFeedBack(any(JsonNode.class), any(JsonNode.class), any(JsonNode.class));
        verify(exerciseAttemptRepository).save(any(ExerciseAttempt.class));
        verify(exerciseResponseMapper).toSubmitExerciseResponse(
                any(Exercise.class), any(), any(), any(), any(), any());
    }

    @Test
    void submitExerciseAttempt_shouldThrowExerciseAttemptNotFoundException_whenAttemptDoesNotExist() {
        SubmitExerciseAttemptRequest request = new SubmitExerciseAttemptRequest(
                ExerciseType.MULTIPLE_CHOICE, new MultipleChoiceAttempt("4"));

        when(exerciseAttemptRepository.findById(exerciseAttemptId)).thenReturn(Optional.empty());

        ExerciseAttemptNotFoundException exception = assertThrows(ExerciseAttemptNotFoundException.class,
                () -> exerciseAttemptService.submitExerciseAttempt(exerciseAttemptId, request));

        assertEquals("Exercise attempt not found", exception.getMessage());

        verify(exerciseAttemptRepository).findById(exerciseAttemptId);
        verify(exerciseAttemptRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toSubmitExerciseResponse(any(), any(), any(), any(), any(), any());
    }

    @Test
    void submitExerciseAttempt_shouldThrowInvalidAttemptTypeException_whenRequestExerciseTypeDoesNotMatchExerciseType() {
        SubmitExerciseAttemptRequest request = new SubmitExerciseAttemptRequest(
                ExerciseType.FILL_IN_BLANK, new MultipleChoiceAttempt("4"));

        when(exerciseAttemptRepository.findById(exerciseAttemptId)).thenReturn(Optional.of(exerciseAttempt));

        InvalidAttemptTypeException exception = assertThrows(InvalidAttemptTypeException.class,
                () -> exerciseAttemptService.submitExerciseAttempt(exerciseAttemptId, request));

        assertEquals("Invalid attempt type exception", exception.getMessage());

        verify(exerciseAttemptRepository).findById(exerciseAttemptId);
        verify(exerciseAttemptRepository, never()).save(any());
    }

    @Test
    void submitExerciseAttempt_shouldThrowInvalidAttemptTypeException_whenAttemptTypeDoesNotMatchExerciseType() {
        FillInBlankAttempt attempt = new FillInBlankAttempt(List.of(
                new FillInBlankSentenceAttempt(sentenceId, "4")));
        SubmitExerciseAttemptRequest request = new SubmitExerciseAttemptRequest(ExerciseType.MULTIPLE_CHOICE, attempt);
        MultipleChoiceContent multipleChoiceContent = new MultipleChoiceContent("What is 2+2?", List.of("4", "5"), "4");

        when(exerciseAttemptRepository.findById(exerciseAttemptId)).thenReturn(Optional.of(exerciseAttempt));
        when(exerciseContentMapper.toExerciseContent(any(JsonNode.class), eq(ExerciseType.MULTIPLE_CHOICE)))
                .thenReturn(multipleChoiceContent);

        InvalidAttemptTypeException exception = assertThrows(InvalidAttemptTypeException.class,
                () -> exerciseAttemptService.submitExerciseAttempt(exerciseAttemptId, request));

        assertEquals("Invalid attempt type exception", exception.getMessage());

        verify(exerciseAttemptRepository).findById(exerciseAttemptId);
        verify(exerciseAttemptRepository, never()).save(any());
    }
}
