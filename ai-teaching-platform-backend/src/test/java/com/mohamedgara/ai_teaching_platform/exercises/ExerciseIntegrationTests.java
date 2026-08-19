package com.mohamedgara.ai_teaching_platform.exercises;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class ExerciseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExerciseGeneratorService exerciseGeneratorService;

    @Test
    void createExercise_shouldCreateExerciseWhenValid() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Map<String, Object> requestBody = Map.of(
                "lesson_id_list", List.of(savedLesson.getId()),
                "type", "MULTIPLE_CHOICE",
                "title", "Addition",
                "instructions", "Choose the correct answer",
                "correct_answers", false,
                "content", Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correct_answer", "4"));

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"))
                .andExpect(jsonPath("$.instructions").value("Choose the correct answer"))
                .andExpect(jsonPath("$.content.question").value("What is 2+2?"));

        Exercise saved = exerciseRepository.findAll().get(0);
        assert Set.of(saved.getLessonIdList()).equals(Set.of(List.of(savedLesson.getId())));
        assert saved.getTitle().equals("Addition");
        assert saved.getType() == ExerciseType.MULTIPLE_CHOICE;
    }

    @Test
    void createExercise_shouldCreateFillInBlankExerciseWhenValid() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Map<String, Object> requestBody = Map.of(
                "lesson_id_list", List.of(savedLesson.getId()),
                "type", "FILL_IN_BLANK",
                "title", "Fill the blanks",
                "instructions", "Complete each sentence",
                "correct_answers", false,
                "content", Map.of(
                        "sentences", List.of(
                                Map.of(
                                        "text", "2 plus 2 is ____",
                                        "answers", List.of("4")))));

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("FILL_IN_BLANK"))
                .andExpect(jsonPath("$.title").value("Fill the blanks"))
                .andExpect(jsonPath("$.instructions").value("Complete each sentence"))
                .andExpect(jsonPath("$.content.sentences[0].text").value("2 plus 2 is ____"));

        Exercise saved = exerciseRepository.findAll().get(0);
        assert Set.of(saved.getLessonIdList()).equals(Set.of(List.of(savedLesson.getId())));
        assert saved.getTitle().equals("Fill the blanks");
        assert saved.getType() == ExerciseType.FILL_IN_BLANK;
    }

    @Test
    void createExercise_shouldReturn404WhenLessonDoesNotExist() throws Exception {
        UUID randomLessonId = UUID.randomUUID();
        Map<String, Object> requestBody = Map.of(
                "lesson_id_list", List.of(randomLessonId),
                "type", "MULTIPLE_CHOICE",
                "title", "Addition",
                "instructions", "Choose the correct answer",
                "correct_answers", false,
                "content", Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correct_answer", "4"));

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createExercise_shouldReturn400WhenTitleIsBlank() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Map<String, Object> requestBody = Map.of(
                "lesson_id_list", List.of(savedLesson.getId()),
                "type", "MULTIPLE_CHOICE",
                "title", "",
                "instructions", "Choose the correct answer",
                "correct_answers", false,
                "content", Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correct_answer", "4"));

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createExercise_shouldReturn400WhenOptionsAreEmpty() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Map<String, Object> requestBody = Map.of(
                "lesson_id_list", List.of(savedLesson.getId()),
                "type", "MULTIPLE_CHOICE",
                "title", "Addition",
                "instructions", "Choose the correct answer",
                "correct_answers", false,
                "content", Map.of(
                        "question", "What is 2+2?",
                        "options", List.of(),
                        "correct_answer", "4"));

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createExercise_shouldCreateExerciseWithMultipleLessonsWhenValid() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourse).build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourse).build());
        Map<String, Object> requestBody = Map.of(
                "lesson_id_list", List.of(savedLessonOne.getId(), savedLessonTwo.getId()),
                "type", "MULTIPLE_CHOICE",
                "title", "Addition",
                "instructions", "Choose the correct answer",
                "correct_answers", false,
                "content", Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correct_answer", "4"));

        mockMvc.perform(post("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lesson_id_list.length()").value(2));

        Exercise saved = exerciseRepository.findAll().get(0);
        assert Set.of(saved.getLessonIdList()).equals(Set.of(List.of(savedLessonOne.getId(), savedLessonTwo.getId())));
    }

    @Test
    void getExercises_shouldReturnExerciseSummaryList_whenCourseHasLessonsWithExercises() throws Exception {
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
                        "correct_answer", "4")))
                .build());

        mockMvc.perform(get("/api/exercise/course/{courseId}", savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$[0].title").value("Addition"))
                .andExpect(jsonPath("$[0].type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$[0].lesson[0].id").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$[0].lesson[0].title").value("Lesson Title"));
    }

    @Test
    void getExercises_shouldReturnOnlyExercisesOfGivenCourse() throws Exception {
        Course savedCourseOne = courseRepository.save(Course.builder().title("Course One").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourseOne).build());
        Exercise savedExerciseOne = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLessonOne.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Exercise One")
                .content(objectMapper.valueToTree(Map.of("question", "What is 2+2?")))
                .build());
        Course savedCourseTwo = courseRepository.save(Course.builder().title("Course Two").build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourseTwo).build());
        exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLessonTwo.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Exercise Two")
                .content(objectMapper.valueToTree(Map.of("question", "What is 2+2?")))
                .build());

        mockMvc.perform(get("/api/exercise/course/{courseId}", savedCourseOne.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(savedExerciseOne.getId().toString()))
                .andExpect(jsonPath("$[0].title").value("Exercise One"))
                .andExpect(jsonPath("$[0].lesson[0].id").value(savedLessonOne.getId().toString()));
    }

    @Test
    void getExercises_shouldReturnExerciseSummaryWithMultipleLessons_whenExerciseHasMultipleLessons() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourse).build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLessonOne.getId(), savedLessonTwo.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .content(objectMapper.valueToTree(Map.of("question", "What is 2+2?")))
                .build());

        mockMvc.perform(get("/api/exercise/course/{courseId}", savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$[0].lesson.length()").value(2))
                .andExpect(jsonPath("$[0].lesson[0].id").value(savedLessonOne.getId().toString()))
                .andExpect(jsonPath("$[0].lesson[0].title").value("Lesson One"))
                .andExpect(jsonPath("$[0].lesson[1].id").value(savedLessonTwo.getId().toString()))
                .andExpect(jsonPath("$[0].lesson[1].title").value("Lesson Two"));
    }

    @Test
    void getExercises_shouldReturnEmptyList_whenCourseHasLessonsButNoExercises() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());

        mockMvc.perform(get("/api/exercise/course/{courseId}", savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getExercises_shouldReturnEmptyList_whenCourseDoesNotExist() throws Exception {
        UUID randomCourseId = UUID.randomUUID();

        mockMvc.perform(get("/api/exercise/course/{courseId}", randomCourseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getExercise_shouldReturnExerciseResponse_whenExerciseExists() throws Exception {
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
                        "correct_answer", "4")))
                .build());

        mockMvc.perform(get("/api/exercise/{id}", savedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"))
                .andExpect(jsonPath("$.instructions").value("Choose the correct answer"))
                .andExpect(jsonPath("$.content.question").value("What is 2+2?"))
                .andExpect(jsonPath("$.content.options[0]").value("4"))
                .andExpect(jsonPath("$.content.options[1]").value("5"))
                .andExpect(jsonPath("$.content.correct_answer").value("4"));
    }

    @Test
    void getExercise_shouldReturnExerciseResponseWithMultipleLessonIds_whenExerciseExists() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourse).build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLessonOne.getId(), savedLessonTwo.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .content(objectMapper.valueToTree(Map.of("question", "What is 2+2?")))
                .build());

        mockMvc.perform(get("/api/exercise/{id}", savedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.lesson_id_list.length()").value(2))
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLessonOne.getId().toString()))
                .andExpect(jsonPath("$.lesson_id_list[1]").value(savedLessonTwo.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"));
    }

    @Test
    void getExercise_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/exercise/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExercise_shouldRemoveExerciseWhenFound() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .content(objectMapper.valueToTree(Map.of("question", "What is 2+2?")))
                .build());

        mockMvc.perform(delete("/api/exercise/{id}", savedExercise.getId()))
                .andExpect(status().isNoContent());

        assert exerciseRepository.count() == 0;
    }

    @Test
    void deleteExercise_shouldRemoveExerciseWithMultipleLessonsWhenFound() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourse).build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLessonOne.getId(), savedLessonTwo.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .content(objectMapper.valueToTree(Map.of("question", "What is 2+2?")))
                .build());

        mockMvc.perform(delete("/api/exercise/{id}", savedExercise.getId()))
                .andExpect(status().isNoContent());

        assert exerciseRepository.count() == 0;
    }

    @Test
    void deleteExercise_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(delete("/api/exercise/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void correctExercise_shouldGenerateAnswersAndReturnUpdatedExerciseWhenFound() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"))))
                .build());

        when(exerciseGeneratorService.generateExerciseAnswer(any())).thenReturn(objectMapper.valueToTree(Map.of(
                "question", "What is 2+2?",
                "options", List.of("4", "5"),
                "correct_answer", "4")));

        mockMvc.perform(patch("/api/exercise/{id}", savedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"))
                .andExpect(jsonPath("$.content.question").value("What is 2+2?"))
                .andExpect(jsonPath("$.content.options[0]").value("4"))
                .andExpect(jsonPath("$.content.options[1]").value("5"))
                .andExpect(jsonPath("$.content.correct_answer").value("4"));

        Exercise updated = exerciseRepository.findById(savedExercise.getId()).orElseThrow();
        assert updated.getContent().get("correct_answer").asText().equals("4");
    }

    @Test
    void correctExercise_shouldReturnExerciseResponseWithMultipleLessonIdsWhenFound() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourse).build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLessonOne.getId(), savedLessonTwo.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .content(objectMapper.valueToTree(Map.of("question", "What is 2+2?")))
                .build());

        when(exerciseGeneratorService.generateExerciseAnswer(any())).thenReturn(objectMapper.valueToTree(Map.of(
                "question", "What is 2+2?",
                "options", List.of("4", "5"),
                "correct_answer", "4")));

        mockMvc.perform(patch("/api/exercise/{id}", savedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.lesson_id_list.length()").value(2))
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLessonOne.getId().toString()))
                .andExpect(jsonPath("$.lesson_id_list[1]").value(savedLessonTwo.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"));
    }

    @Test
    void correctExercise_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(patch("/api/exercise/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void generateExercise_shouldBuildAndReturnExerciseResponse_whenCourseIdIsProvided() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").content("Content One").course(savedCourse).build());

        when(exerciseGeneratorService.generateExercise(any(), any())).thenReturn(
                new com.mohamedgara.ai_teaching_platform.AI.dto.GeneratedExercise(
                        "Generated Title",
                        "Generated instructions",
                        objectMapper.valueToTree(Map.of(
                                "question", "What is 2+2?",
                                "options", List.of("4"),
                                "correct_answer", "4"))
                )
        );

        Map<String, Object> requestBody = Map.of(
                "lesson_id_list", List.of(),
                "course_id", savedCourse.getId(),
                "type", "MULTIPLE_CHOICE"
        );

        mockMvc.perform(post("/api/exercise/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Generated Title"))
                .andExpect(jsonPath("$.instructions").value("Generated instructions"))
                .andExpect(jsonPath("$.content.question").value("What is 2+2?"));

        Exercise saved = exerciseRepository.findAll().get(0);
        assert saved.getTitle().equals("Generated Title");
        assert saved.getContent().get("question").asText().equals("What is 2+2?");
    }

    @Test
    void generateExercise_shouldBuildAndReturnExerciseResponse_whenCourseIdIsNullAndLessonIdsAreProvided() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").content("Content One").course(savedCourse).build());

        when(exerciseGeneratorService.generateExercise(any(), any())).thenReturn(
                new com.mohamedgara.ai_teaching_platform.AI.dto.GeneratedExercise(
                        "Generated Title",
                        "Generated instructions",
                        objectMapper.valueToTree(Map.of(
                                "question", "What is 2+2?",
                                "options", List.of("4"),
                                "correct_answer", "4"))
                )
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("lesson_id_list", List.of(savedLesson.getId()));
        requestBody.put("course_id", null);
        requestBody.put("type", "MULTIPLE_CHOICE");

        mockMvc.perform(post("/api/exercise/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.lesson_id_list[0]").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Generated Title"))
                .andExpect(jsonPath("$.instructions").value("Generated instructions"))
                .andExpect(jsonPath("$.content.question").value("What is 2+2?"));

        Exercise saved = exerciseRepository.findAll().get(0);
        assert saved.getTitle().equals("Generated Title");
        assert saved.getContent().get("question").asText().equals("What is 2+2?");
    }

    @Test
    void generateExercise_shouldReturnBadRequest_whenNoLessonReferenceProvided() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("lesson_id_list", List.of());
        requestBody.put("course_id", null);
        requestBody.put("type", "MULTIPLE_CHOICE");

        mockMvc.perform(post("/api/exercise/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }
}
