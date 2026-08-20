package com.mohamedgara.ai_teaching_platform.exercises;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseAttemptRepository;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class ExerciseAttemptIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseAttemptRepository exerciseAttemptRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExerciseGeneratorService exerciseGeneratorService;

    @Test
    void startExerciseAttempt_shouldCreateAttemptAndReturnResponseWhenExerciseExists() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correctAnswer", "4")))
                .build());

        mockMvc.perform(post("/api/exercise-attempt/{exerciseId}/attempt", savedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.exercise_attempt_id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"))
                .andExpect(jsonPath("$.instructions").value("Choose the correct answer"))
                .andExpect(jsonPath("$.content.question").value("What is 2+2?"))
                .andExpect(jsonPath("$.content.options[0]").value("4"))
                .andExpect(jsonPath("$.content.options[1]").value("5"));

        assert exerciseAttemptRepository.count() == 1;
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.findAll().get(0);
        assert savedAttempt.getExercise().getId().equals(savedExercise.getId());
    }

    @Test
    void startExerciseAttempt_shouldCreateAttemptAndReturnFillInBlankContentWithSentenceIds_whenExerciseExists() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.FILL_IN_BLANK)
                .title("Fill the blanks")
                .instructions("Complete each sentence")
                .content(objectMapper.valueToTree(Map.of(
                        "sentences", List.of(
                                Map.of(
                                        "id", UUID.randomUUID().toString(),
                                        "text", "2 plus 2 is ____",
                                        "answers", List.of("4"))))))
                .build());

        mockMvc.perform(post("/api/exercise-attempt/{exerciseId}/attempt", savedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.exercise_attempt_id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("FILL_IN_BLANK"))
                .andExpect(jsonPath("$.title").value("Fill the blanks"))
                .andExpect(jsonPath("$.instructions").value("Complete each sentence"))
                .andExpect(jsonPath("$.content.sentences[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content.sentences[0].text").value("2 plus 2 is ____"));

        assert exerciseAttemptRepository.count() == 1;
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.findAll().get(0);
        assert savedAttempt.getExercise().getId().equals(savedExercise.getId());
    }

    @Test
    void startExerciseAttempt_shouldReturn404WhenExerciseDoesNotExist() throws Exception {
        UUID randomExerciseId = UUID.randomUUID();

        mockMvc.perform(post("/api/exercise-attempt/{exerciseId}/attempt", randomExerciseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitExerciseAttempt_shouldSubmitMultipleChoiceAndReturnResponse_whenAnswerIsCorrect() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correctAnswer", "4")))
                .build());
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.save(ExerciseAttempt.builder()
                .exercise(savedExercise)
                .build());
        Map<String, Object> requestBody = Map.of(
                "exercise_type", "MULTIPLE_CHOICE",
                "attempt", Map.of("answer", "4"));

        when(exerciseGeneratorService.generateAttemptFeedBack(any(), any(), any())).thenReturn("Great job!");

        mockMvc.perform(post("/api/exercise-attempt/{exerciseAttemptId}/submit", savedAttempt.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attempt_id").value(savedAttempt.getId().toString()))
                .andExpect(jsonPath("$.exercise_id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"))
                .andExpect(jsonPath("$.instructions").value("Choose the correct answer"))
                .andExpect(jsonPath("$.compared_answer.question").value("What is 2+2?"))
                .andExpect(jsonPath("$.compared_answer.options[0]").value("4"))
                .andExpect(jsonPath("$.compared_answer.options[1]").value("5"))
                .andExpect(jsonPath("$.compared_answer.correct_answers").value("4"))
                .andExpect(jsonPath("$.compared_answer.submitted_answer").value("4"))
                .andExpect(jsonPath("$.compared_answer.is_correct").value(true))
                .andExpect(jsonPath("$.score").value(10))
                .andExpect(jsonPath("$.ai_feedback").value("Great job!"));

        ExerciseAttempt updatedAttempt = exerciseAttemptRepository.findById(savedAttempt.getId()).orElseThrow();
        assert updatedAttempt.getScore() == 10;
        assert updatedAttempt.getAiFeedback().equals("Great job!");
        assert updatedAttempt.getSubmittedAt() != null;
        assert updatedAttempt.getUserAnswer() != null;
    }

    @Test
    void submitExerciseAttempt_shouldSubmitMultipleChoiceWithScoreZero_whenAnswerIsIncorrect() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correctAnswer", "4")))
                .build());
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.save(ExerciseAttempt.builder()
                .exercise(savedExercise)
                .build());
        Map<String, Object> requestBody = Map.of(
                "exercise_type", "MULTIPLE_CHOICE",
                "attempt", Map.of("answer", "5"));

        when(exerciseGeneratorService.generateAttemptFeedBack(any(), any(), any())).thenReturn("Try again!");

        mockMvc.perform(post("/api/exercise-attempt/{exerciseAttemptId}/submit", savedAttempt.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attempt_id").value(savedAttempt.getId().toString()))
                .andExpect(jsonPath("$.compared_answer.submitted_answer").value("5"))
                .andExpect(jsonPath("$.compared_answer.is_correct").value(false))
                .andExpect(jsonPath("$.score").value(0))
                .andExpect(jsonPath("$.ai_feedback").value("Try again!"));

        ExerciseAttempt updatedAttempt = exerciseAttemptRepository.findById(savedAttempt.getId()).orElseThrow();
        assert updatedAttempt.getScore() == 0;
    }

    @Test
    void submitExerciseAttempt_shouldSubmitFillInBlankAndReturnResponse_whenAllAnswersCorrect() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        UUID sentenceId = UUID.randomUUID();
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.FILL_IN_BLANK)
                .title("Fill the blanks")
                .instructions("Complete each sentence")
                .content(objectMapper.valueToTree(Map.of(
                        "sentences", List.of(
                                Map.of(
                                        "id", sentenceId.toString(),
                                        "text", "2 plus 2 is ____",
                                        "answers", List.of("4"))))))
                .build());
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.save(ExerciseAttempt.builder()
                .exercise(savedExercise)
                .build());
        Map<String, Object> requestBody = Map.of(
                "exercise_type", "FILL_IN_BLANK",
                "attempt", Map.of("sentences", List.of(
                        Map.of("sentence_id", sentenceId.toString(), "answer", "4"))));

        when(exerciseGeneratorService.generateAttemptFeedBack(any(), any(), any())).thenReturn("Great job!");

        mockMvc.perform(post("/api/exercise-attempt/{exerciseAttemptId}/submit", savedAttempt.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attempt_id").value(savedAttempt.getId().toString()))
                .andExpect(jsonPath("$.exercise_id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.type").value("FILL_IN_BLANK"))
                .andExpect(jsonPath("$.title").value("Fill the blanks"))
                .andExpect(jsonPath("$.compared_answer.sentences[0].text").value("2 plus 2 is ____"))
                .andExpect(jsonPath("$.compared_answer.sentences[0].submitted_answer").value("4"))
                .andExpect(jsonPath("$.compared_answer.sentences[0].is_correct").value(true))
                .andExpect(jsonPath("$.score").value(10))
                .andExpect(jsonPath("$.ai_feedback").value("Great job!"));

        ExerciseAttempt updatedAttempt = exerciseAttemptRepository.findById(savedAttempt.getId()).orElseThrow();
        assert updatedAttempt.getScore() == 10;
    }

    @Test
    void submitExerciseAttempt_shouldSubmitFillInBlankWithPartialScore_whenSomeAnswersIncorrect() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        UUID sentenceIdOne = UUID.randomUUID();
        UUID sentenceIdTwo = UUID.randomUUID();
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.FILL_IN_BLANK)
                .title("Fill the blanks")
                .instructions("Complete each sentence")
                .content(objectMapper.valueToTree(Map.of(
                        "sentences", List.of(
                                Map.of(
                                        "id", sentenceIdOne.toString(),
                                        "text", "2 plus 2 is ____",
                                        "answers", List.of("4")),
                                Map.of(
                                        "id", sentenceIdTwo.toString(),
                                        "text", "2 times 2 is ____",
                                        "answers", List.of("4"))))))
                .build());
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.save(ExerciseAttempt.builder()
                .exercise(savedExercise)
                .build());
        Map<String, Object> requestBody = Map.of(
                "exercise_type", "FILL_IN_BLANK",
                "attempt", Map.of("sentences", List.of(
                        Map.of("sentence_id", sentenceIdOne.toString(), "answer", "4"),
                        Map.of("sentence_id", sentenceIdTwo.toString(), "answer", "5"))));

        when(exerciseGeneratorService.generateAttemptFeedBack(any(), any(), any())).thenReturn("Partial credit");

        mockMvc.perform(post("/api/exercise-attempt/{exerciseAttemptId}/submit", savedAttempt.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compared_answer.sentences[0].is_correct").value(true))
                .andExpect(jsonPath("$.compared_answer.sentences[1].is_correct").value(false))
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.ai_feedback").value("Partial credit"));

        ExerciseAttempt updatedAttempt = exerciseAttemptRepository.findById(savedAttempt.getId()).orElseThrow();
        assert updatedAttempt.getScore() == 5;
    }

    @Test
    void submitExerciseAttempt_shouldReturn404WhenAttemptDoesNotExist() throws Exception {
        UUID randomAttemptId = UUID.randomUUID();
        Map<String, Object> requestBody = Map.of(
                "exercise_type", "MULTIPLE_CHOICE",
                "attempt", Map.of("answer", "4"));

        mockMvc.perform(post("/api/exercise-attempt/{exerciseAttemptId}/submit", randomAttemptId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitExerciseAttempt_shouldReturn400_whenRequestExerciseTypeDoesNotMatchExerciseType() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correctAnswer", "4")))
                .build());
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.save(ExerciseAttempt.builder()
                .exercise(savedExercise)
                .build());
        Map<String, Object> requestBody = Map.of(
                "exercise_type", "FILL_IN_BLANK",
                "attempt", Map.of("answer", "4"));

        mockMvc.perform(post("/api/exercise-attempt/{exerciseAttemptId}/submit", savedAttempt.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitExerciseAttempt_shouldReturn400_whenAttemptTypeDoesNotMatchExerciseType() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correctAnswer", "4")))
                .build());
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.save(ExerciseAttempt.builder()
                .exercise(savedExercise)
                .build());
        UUID sentenceId = UUID.randomUUID();
        Map<String, Object> requestBody = Map.of(
                "exercise_type", "MULTIPLE_CHOICE",
                "attempt", Map.of("sentences", List.of(
                        Map.of("sentence_id", sentenceId.toString(), "answer", "4"))));

        mockMvc.perform(post("/api/exercise-attempt/{exerciseAttemptId}/submit", savedAttempt.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }
}