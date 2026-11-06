package com.mohamedgara.ai_teaching_platform.courses;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateLessonRequest;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseVisibility;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseCategoryRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.UpdateCourseRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class CourseIntegrationTests {

    private static final String OWNER_SUB = "auth0|owner-1";
    private static final String OWNER_SUB2 = "auth0|owner-2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    @Autowired
    private LessonRepository lessonRepository;



    @Autowired
    private ObjectMapper objectMapper;


    private RequestPostProcessor asOwner() {
        return jwt().jwt(j -> j.subject(OWNER_SUB));
    }

    private RequestPostProcessor asOwner2(){
        return jwt().jwt(j-> j.subject(OWNER_SUB2));
    }

    CourseCategory courseCategory;

    @BeforeEach
    void setUp(){
        courseCategory = courseCategoryRepository.save(CourseCategory.builder()
                .name("Programming")
                .slug("programming")
                .displayOrder(1)
                .build());
    }

    @Test
    void createCourse_shouldReturn401_whenNoTokenProvided() throws Exception{

        CreateCourseRequest request = new CreateCourseRequest(
                "Intro to Spring Boot",
                courseCategory.getId(),
                CourseVisibility.PUBLIC
        );

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCourse_shouldReturn201AndPersistCourse() throws Exception{

        CreateCourseRequest request = new CreateCourseRequest(
                "Intro to Spring Boot",
                courseCategory.getId(),
                CourseVisibility.PUBLIC
        );

        mockMvc.perform(post("/api/course")
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Intro to Spring Boot"))
                .andExpect(jsonPath("$.category.id").value(courseCategory.getId().toString()))
                .andExpect(jsonPath("$.category.name").value("Programming"))
                .andExpect(jsonPath("$.category.slug").value("programming"))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"))
                .andExpect(jsonPath("$.created_at").exists());

        assert courseRepository.count() == 1;

        Course savedCourse = courseRepository.findAll().get(0);

        assert (savedCourse.getTitle()).equals("Intro to Spring Boot");
        assert (savedCourse.getVisibility()).equals(CourseVisibility.PUBLIC);
        assert (savedCourse.getCategory().getId()).equals(courseCategory.getId());

        // Most importantly: Verify the relationship/owner came from the JWT token, not the request body!
        assert (savedCourse.getOwnerId()).equals(OWNER_SUB);

    }

    @Test
    void createCourse_shouldReturn400_whenTitleIsBlank() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest(
                "",
                UUID.randomUUID(),
                CourseVisibility.PUBLIC
        );

        mockMvc.perform(post("/api/course")
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        assert courseRepository.count() == 0;
    }

    @Test
    void createCourse_shouldReturn400_whenTitleTooShort() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest(
                "ab",
                UUID.randomUUID(),
                CourseVisibility.PUBLIC
        );

        mockMvc.perform(post("/api/course")
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_shouldReturn404_whenCategoryNotFound() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest(
                "JAVA",
                UUID.randomUUID(),
                CourseVisibility.PUBLIC
        );

        mockMvc.perform(post("/api/course")
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCourseList_shouldReturn401_whenNoTokenProvided() throws Exception{

        courseRepository.save(Course.builder()
                .title("Course A")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());
        courseRepository.save(Course.builder()
                .title("Course B")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        mockMvc.perform(get("/api/course"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCourseList_shouldReturnAllCourses() throws Exception {
        courseRepository.save(Course.builder()
                .title("Course A")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());
        courseRepository.save(Course.builder()
                .title("Course B")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        mockMvc.perform(get("/api/course")
                        .with(asOwner())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Course A", "Course B")));
    }

    @Test
    void getCourseList_shouldReturnOnlyCorrectOwnerCourses() throws Exception{
        courseRepository.save(Course.builder()
                .title("Course A")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        courseRepository.save(Course.builder()
                .title("Course B")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB2)
                .build());

        mockMvc.perform(get("/api/course")
                        .with(asOwner())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Course A")));
    }

    @Test
    void deleteCourse_shouldReturn401_whenNoTokenProvided() throws Exception{

        Course saved = courseRepository.save(Course.builder()
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .title("To Delete")
                .ownerId(OWNER_SUB).build());

        mockMvc.perform(delete("/api/course/{id}", saved.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCourse_shouldRemoveCourseWhenFound() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .title("To Delete")
                .ownerId(OWNER_SUB).build());

        mockMvc.perform(delete("/api/course/{id}", saved.getId())
                        .with(asOwner())
                )
                .andExpect(status().isNoContent());

        assert courseRepository.count() == 0;
    }

    @Test
    void deleteCourse_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(delete("/api/course/{id}", randomId)
                        .with(asOwner())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourse_shouldRemoveCourse_whenOwnerOwnsIt() throws Exception {
        Course saved = courseRepository.save(
                Course.builder().title("To Delete")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(OWNER_SUB).build());

        mockMvc.perform(delete("/api/course/{id}", saved.getId())
                        .with(asOwner()))
                .andExpect(status().isNoContent());

        assert courseRepository.findById(saved.getId()).isEmpty();
    }

    @Test
    void deleteCourse_shouldReturn404_whenCourseBelongsToAnotherOwner() throws Exception {
        Course saved = courseRepository.save(
                Course.builder().title("Not yours")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(OWNER_SUB2).build());

        mockMvc.perform(delete("/api/course/{id}", saved.getId())
                        .with(asOwner()))
                .andExpect(status().isNotFound());   // or isForbidden(), whichever your API returns

        assert courseRepository.findById(saved.getId()).isPresent();
    }

    @Test
    void deleteCourse_shouldOnlyDeleteTheRequestedCourse() throws Exception {
        Course toDelete = courseRepository.save(
                Course.builder()
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .title("To Delete")
                        .ownerId(OWNER_SUB).build());
        Course toKeep = courseRepository.save(
                Course.builder()
                        .title("To Keep")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(OWNER_SUB).build());

        mockMvc.perform(delete("/api/course/{id}", toDelete.getId())
                        .with(asOwner()))
                .andExpect(status().isNoContent());

        assert courseRepository.findById(toDelete.getId()).isEmpty();
        assert courseRepository.findById(toKeep.getId()).isPresent();
    }

    @Test
    void deleteCourse_shouldReturn400_whenIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(delete("/api/course/{id}", "not-a-uuid")
                        .with(asOwner()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCourse_shouldReturn404_whenDeletedTwice() throws Exception {
        Course saved = courseRepository.save(
                Course.builder()
                        .title("Delete me twice")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(OWNER_SUB).build());

        mockMvc.perform(delete("/api/course/{id}", saved.getId()).with(asOwner()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/course/{id}", saved.getId()).with(asOwner()))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateCourse_shouldReturn401_whenNoTokenProvided() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Original Title")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        UpdateCourseRequest request = new UpdateCourseRequest(
                "Updated Title",
                null,
                null
        );

        mockMvc.perform(patch("/api/course/{courseId}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateCourse_shouldReturn200AndPersistUpdatedCourse() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Original Title")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        CourseCategory newCategory = courseCategoryRepository.save(CourseCategory.builder()
                .name("Advanced Programming")
                .slug("advanced-programming")
                .displayOrder(2)
                .build());

        UpdateCourseRequest request = new UpdateCourseRequest(
                "Updated Title",
                newCategory.getId(),
                CourseVisibility.PRIVATE
        );

        mockMvc.perform(patch("/api/course/{courseId}", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.category.id").value(newCategory.getId().toString()))
                .andExpect(jsonPath("$.category.name").value("Advanced Programming"))
                .andExpect(jsonPath("$.category.slug").value("advanced-programming"))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"));

        Course updatedCourse = courseRepository.findById(saved.getId()).get();
        assert updatedCourse.getTitle().equals("Updated Title");
        assert updatedCourse.getVisibility().equals(CourseVisibility.PRIVATE);
        assert updatedCourse.getCategory().getId().equals(newCategory.getId());
    }

    @Test
    void updateCourse_shouldPartiallyUpdateCourse_whenSomeFieldsAreNull() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Original Title")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        // Only updating the title, leaving category and visibility null
        UpdateCourseRequest request = new UpdateCourseRequest(
                "New Title Only",
                null,
                null
        );

        mockMvc.perform(patch("/api/course/{courseId}", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title Only"))
                .andExpect(jsonPath("$.category.name").value("Programming"))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"));

        Course updatedCourse = courseRepository.findById(saved.getId()).get();
        assert updatedCourse.getTitle().equals("New Title Only");
        assert updatedCourse.getVisibility().equals(CourseVisibility.PUBLIC);
        assert updatedCourse.getCategory().getId().equals(courseCategory.getId());
    }

    @Test
    void updateCourse_shouldReturn400_whenTitleIsTooShort() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Original Title")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        UpdateCourseRequest request = new UpdateCourseRequest(
                "ab",
                null,
                null
        );

        mockMvc.perform(patch("/api/course/{courseId}", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCourse_shouldReturn404_whenCourseNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        UpdateCourseRequest request = new UpdateCourseRequest(
                "Updated Title",
                null,
                null
        );

        mockMvc.perform(patch("/api/course/{courseId}", randomId)
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCourse_shouldReturn404_whenCourseBelongsToAnotherOwner() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Someone Else's Course")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB2)
                .build());

        UpdateCourseRequest request = new UpdateCourseRequest(
                "Hacked Title",
                null,
                null
        );

        mockMvc.perform(patch("/api/course/{courseId}", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Verify the title wasn't modified
        Course unmodified = courseRepository.findById(saved.getId()).get();
        assert unmodified.getTitle().equals("Someone Else's Course");
    }

    @Test
    void updateCourse_shouldReturn404_whenCategoryNotFound() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Original Title")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        UpdateCourseRequest request = new UpdateCourseRequest(
                null,
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(patch("/api/course/{courseId}", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCourse_shouldReturn400_whenCourseIdIsNotAValidUuid() throws Exception {
        UpdateCourseRequest request = new UpdateCourseRequest(
                "Updated Title",
                null,
                null
        );

        mockMvc.perform(patch("/api/course/{courseId}", "not-a-uuid")
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLesson_shouldReturn201AndPersistLesson() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Spring Boot Course")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(OWNER_SUB)
                .build());
        CreateLessonRequest request = new CreateLessonRequest("Introduction to Controllers");

        mockMvc.perform(post("/api/course/{courseId}/lesson", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Introduction to Controllers"))
                .andExpect(jsonPath("$.course_id").value(saved.getId().toString()));

        assert lessonRepository.count() == 1;
    }

        @Test
        void createLesson_shouldReturn401_whenNoTokenProvided() throws Exception {
                Course saved = courseRepository.save(Course.builder()
                                .title("Spring Boot Course")
                                .category(courseCategory)
                                .visibility(CourseVisibility.PUBLIC)
                                .ownerId(OWNER_SUB)
                                .build());
                CreateLessonRequest request = new CreateLessonRequest("Introduction to Controllers");

                mockMvc.perform(post("/api/course/{courseId}/lesson", saved.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

    @Test
    void createLesson_shouldReturn400WhenTitleIsBlank() throws Exception {
                Course saved = courseRepository.save(Course.builder()
                                .title("Spring Boot Course")
                                .category(courseCategory)
                                .visibility(CourseVisibility.PUBLIC)
                                .ownerId(OWNER_SUB)
                                .build());
        CreateLessonRequest request = new CreateLessonRequest("");

        mockMvc.perform(post("/api/course/{courseId}/lesson", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assert lessonRepository.count() == 0;
    }

    @Test
    void createLesson_shouldReturn400WhenTitleTooShort() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Spring Boot Course")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());
        CreateLessonRequest request = new CreateLessonRequest("ab");

        mockMvc.perform(post("/api/course/{courseId}/lesson", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLesson_shouldReturn404WhenCourseNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        CreateLessonRequest request = new CreateLessonRequest("Introduction to Controllers");

        mockMvc.perform(post("/api/course/{courseId}/lesson", randomId)
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createLesson_shouldReturn404WhenCourseBelongsToAnotherOwner() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Another Owner's Course")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB2)
                .build());
        CreateLessonRequest request = new CreateLessonRequest("Introduction to Controllers");

        mockMvc.perform(post("/api/course/{courseId}/lesson", saved.getId())
                        .with(asOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assert lessonRepository.count() == 0;
    }

    @Test
    void getCourseLessonList_shouldReturnAllLessonsForCourse() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Spring Boot Course")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(OWNER_SUB)
                .build());
        lessonRepository.save(Lesson.builder().title("Lesson 1").course(saved).build());
        lessonRepository.save(Lesson.builder().title("Lesson 2").course(saved).build());

        mockMvc.perform(get("/api/course/{courseId}/lesson", saved.getId()).with(asOwner()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Lesson 1", "Lesson 2")))
                .andExpect(jsonPath("$[*].course_id", containsInAnyOrder(saved.getId().toString(), saved.getId().toString())));
    }

    @Test
    void getCourseLessonList_shouldReturn401_whenNoTokenProvided() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Spring Boot Course")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        mockMvc.perform(get("/api/course/{courseId}/lesson", saved.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCourseLessonList_shouldReturnEmptyListWhenNoLessonsExist() throws Exception {
        Course saved = courseRepository.save(Course.builder()
                .title("Empty Course")
                .category(courseCategory)
                .visibility(CourseVisibility.PUBLIC)
                .ownerId(OWNER_SUB)
                .build());

        mockMvc.perform(get("/api/course/{courseId}/lesson", saved.getId()).with(asOwner()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getCourseLessonList_shouldReturn404WhenCourseNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/course/{courseId}/lesson", randomId).with(asOwner()))
                .andExpect(status().isNotFound());
    }

        @Test
        void getCourseLessonList_shouldReturn404WhenCourseBelongsToAnotherOwner() throws Exception {
                Course saved = courseRepository.save(Course.builder()
                                .title("Another Owner's Course")
                                .category(courseCategory)
                                .visibility(CourseVisibility.PUBLIC)
                                .ownerId(OWNER_SUB2)
                                .build());

                mockMvc.perform(get("/api/course/{courseId}/lesson", saved.getId()).with(asOwner()))
                                .andExpect(status().isNotFound());
        }
}
