package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateLessonRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonSummaryResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseVisibility;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.exception.CourseCategoryNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mohamedgara.ai_teaching_platform.courses.repository.CourseCategoryRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.UpdateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.exception.CourseNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private CourseCategoryRepository courseCategoryRepository;
    @Spy
    private final CourseResponseMapper courseResponseMapper = new CourseResponseMapperImpl();
    @Spy
    private final CourseRequestMapper courseRequestMapper = new CourseRequestMapperImpl();
    @Spy
    private final LessonResponseMapper lessonResponseMapper = new LessonResponseMapperImpl();
    @InjectMocks
    private CourseService courseService;

    @Test
    public void createCourse_shouldReturnCourseResponse_whenCourseRequestAllFieldsExists(){

        //Arrange
        UUID courseId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String authenticatedUserId = "1";
        CourseCategory category = CourseCategory.builder()
                .id(categoryId)
                .name("java")
                .slug("java")
                .build();

        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> {
                    Course course = invocationOnMock.getArgument(0);
                    course.setId(courseId);
                    return course;
                });
        Mockito.when(courseCategoryRepository.findById(categoryId))
                        .thenReturn(Optional.of(category));


        CreateCourseRequest createCourseRequest = new CreateCourseRequest(
                "title example",
                categoryId,
                CourseVisibility.PUBLIC
        );

        //Act
        CourseResponse result = courseService.createCourse(authenticatedUserId,createCourseRequest);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(courseId);
        Assertions.assertThat(result.title()).isEqualTo(createCourseRequest.title());

        Assertions.assertThat(result.category().id()).isEqualTo(category.getId());
        Assertions.assertThat(result.category().name()).isEqualTo(category.getName());
        Assertions.assertThat(result.category().slug()).isEqualTo(category.getSlug());

        Assertions.assertThat(result.visibility()).isEqualTo(CourseVisibility.PUBLIC);

        Mockito.verify(courseRepository).save(Mockito.any(Course.class));
        Mockito.verify(courseResponseMapper).toCourseResponse(Mockito.any(Course.class));
        Mockito.verify(courseRequestMapper).toCourse(createCourseRequest);
        Mockito.verify(courseCategoryRepository).findById(categoryId);

    }

    @Test
    public void createCourse_shouldReturnException_whenCategoryNotFound(){
        //Arrange
        UUID categoryId = UUID.randomUUID();
        String authenticatedUserId = "1";
        CreateCourseRequest createCourseRequest = new CreateCourseRequest(
                "title example",
                categoryId,
                CourseVisibility.PUBLIC
        );

        Mockito.when(courseCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        //Act & Assert
        CourseCategoryNotFoundException exception = assertThrows(
                CourseCategoryNotFoundException.class,
                () -> courseService.createCourse(authenticatedUserId, createCourseRequest)
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Course category not found");

        Mockito.verify(courseCategoryRepository).findById(categoryId);
        Mockito.verify(courseRepository, Mockito.never()).save(Mockito.any(Course.class));
        Mockito.verify(courseResponseMapper, Mockito.never()).toCourseResponse(Mockito.any(Course.class));
    }

    @Test
    public void getCourseList_shouldReturnCourseResponseList_whenCoursesExist(){
        //Arrange
        String userId = "id1";
        UUID courseId1 = UUID.randomUUID();
        UUID courseId2 = UUID.randomUUID();

        Course course1 = Course.builder().id(courseId1).title("Course 1").build();
        Course course2 = Course.builder().id(courseId2).title("Course 2").build();

        List<Course> courseList = List.of(course1, course2);

        Mockito.when(courseRepository.findCourseByOwnerId(userId)).thenReturn(courseList);

        //Act
        List<CourseResponse> result = courseService.getCourseList(userId);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).id()).isEqualTo(courseId1);
        Assertions.assertThat(result.get(0).title()).isEqualTo("Course 1");
        Assertions.assertThat(result.get(1).id()).isEqualTo(courseId2);
        Assertions.assertThat(result.get(1).title()).isEqualTo("Course 2");

        Mockito.verify(courseRepository).findCourseByOwnerId(userId);
        Mockito.verify(courseResponseMapper).toCourseListResponse(courseList);
    }

    @Test
    public void getCourseList_shouldReturnEmptyList_whenNoCoursesExist(){
        //Arrange
        String userId = "id1";
        List<Course> emptyList = List.of();

        Mockito.when(courseRepository.findCourseByOwnerId(userId)).thenReturn(emptyList);

        //Act
        List<CourseResponse> result = courseService.getCourseList(userId);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEmpty();

        Mockito.verify(courseRepository).findCourseByOwnerId(userId);
        Mockito.verify(courseResponseMapper).toCourseListResponse(emptyList);
    }



    @Test
    public void deleteCourse_shouldDeleteCourse_whenCourseExists(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("title example").ownerId(ownerId).build();

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId,courseId)).thenReturn(Optional.of(course));

        //Act
        courseService.deleteCourse(ownerId,courseId);

        //Assert
        Mockito.verify(courseRepository).findCourseByOwnerIdAndCourseId(ownerId,courseId);
        Mockito.verify(courseRepository).delete(course);
    }

    @Test
    public void deleteCourse_shouldThrowCourseNotFoundException_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        String ownerId = "id";

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId,courseId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.deleteCourse(ownerId,courseId));

        Mockito.verify(courseRepository).findCourseByOwnerIdAndCourseId(ownerId, courseId);
    }

    @Test
    public void updateCourse_shouldUpdateAllFields_whenAllFieldsAreProvided(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        UUID oldCategoryId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();

        CourseCategory oldCategory = CourseCategory.builder()
                .id(oldCategoryId).name("java").slug("java").build();
        CourseCategory newCategory = CourseCategory.builder()
                .id(newCategoryId).name("angular").slug("angular").build();
        Course course = Course.builder()
                .id(courseId)
                .title("old title")
                .ownerId(ownerId)
                .category(oldCategory)
                .visibility(CourseVisibility.PRIVATE)
                .build();

        UpdateCourseRequest updateCourseRequest = new UpdateCourseRequest(
                "new title",
                newCategoryId,
                CourseVisibility.PUBLIC
        );

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId, courseId))
                .thenReturn(Optional.of(course));
        Mockito.when(courseCategoryRepository.findById(newCategoryId))
                .thenReturn(Optional.of(newCategory));
        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        CourseResponse result = courseService.updateCourse(ownerId, courseId, updateCourseRequest);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(courseId);
        Assertions.assertThat(result.title()).isEqualTo("new title");
        Assertions.assertThat(result.category().id()).isEqualTo(newCategoryId);
        Assertions.assertThat(result.category().name()).isEqualTo("angular");
        Assertions.assertThat(result.visibility()).isEqualTo(CourseVisibility.PUBLIC);

        Mockito.verify(courseRepository).findCourseByOwnerIdAndCourseId(ownerId, courseId);
        Mockito.verify(courseCategoryRepository).findById(newCategoryId);
        Mockito.verify(courseRepository).save(course);
        Mockito.verify(courseResponseMapper).toCourseResponse(course);
    }

    @Test
    public void updateCourse_shouldUpdateOnlyTitle_whenOnlyTitleIsProvided(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        CourseCategory category = CourseCategory.builder()
                .id(UUID.randomUUID()).name("java").slug("java").build();
        Course course = Course.builder()
                .id(courseId)
                .title("old title")
                .ownerId(ownerId)
                .category(category)
                .visibility(CourseVisibility.PRIVATE)
                .build();

        UpdateCourseRequest updateCourseRequest = new UpdateCourseRequest("new title", null, null);

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId, courseId))
                .thenReturn(Optional.of(course));
        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        CourseResponse result = courseService.updateCourse(ownerId, courseId, updateCourseRequest);

        //Assert
        Assertions.assertThat(result.title()).isEqualTo("new title");
        Assertions.assertThat(result.category().id()).isEqualTo(category.getId());
        Assertions.assertThat(result.visibility()).isEqualTo(CourseVisibility.PRIVATE);

        Mockito.verify(courseCategoryRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(courseRepository).save(course);
    }

    @Test
    public void updateCourse_shouldUpdateOnlyCategory_whenOnlyCategoryIsProvided(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();
        CourseCategory oldCategory = CourseCategory.builder()
                .id(UUID.randomUUID()).name("java").slug("java").build();
        CourseCategory newCategory = CourseCategory.builder()
                .id(newCategoryId).name("angular").slug("angular").build();
        Course course = Course.builder()
                .id(courseId)
                .title("old title")
                .ownerId(ownerId)
                .category(oldCategory)
                .visibility(CourseVisibility.PRIVATE)
                .build();

        UpdateCourseRequest updateCourseRequest = new UpdateCourseRequest(null, newCategoryId, null);

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId, courseId))
                .thenReturn(Optional.of(course));
        Mockito.when(courseCategoryRepository.findById(newCategoryId))
                .thenReturn(Optional.of(newCategory));
        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        CourseResponse result = courseService.updateCourse(ownerId, courseId, updateCourseRequest);

        //Assert
        Assertions.assertThat(result.title()).isEqualTo("old title");
        Assertions.assertThat(result.category().id()).isEqualTo(newCategoryId);
        Assertions.assertThat(result.visibility()).isEqualTo(CourseVisibility.PRIVATE);

        Mockito.verify(courseCategoryRepository).findById(newCategoryId);
        Mockito.verify(courseRepository).save(course);
    }

    @Test
    public void updateCourse_shouldUpdateOnlyVisibility_whenOnlyVisibilityIsProvided(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        CourseCategory category = CourseCategory.builder()
                .id(UUID.randomUUID()).name("java").slug("java").build();
        Course course = Course.builder()
                .id(courseId)
                .title("old title")
                .ownerId(ownerId)
                .category(category)
                .visibility(CourseVisibility.PRIVATE)
                .build();

        UpdateCourseRequest updateCourseRequest = new UpdateCourseRequest(null, null, CourseVisibility.PUBLIC);

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId, courseId))
                .thenReturn(Optional.of(course));
        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        CourseResponse result = courseService.updateCourse(ownerId, courseId, updateCourseRequest);

        //Assert
        Assertions.assertThat(result.title()).isEqualTo("old title");
        Assertions.assertThat(result.category().id()).isEqualTo(category.getId());
        Assertions.assertThat(result.visibility()).isEqualTo(CourseVisibility.PUBLIC);

        Mockito.verify(courseCategoryRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(courseRepository).save(course);
    }

    @Test
    public void updateCourse_shouldKeepAllFields_whenNoFieldIsProvided(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        CourseCategory category = CourseCategory.builder()
                .id(UUID.randomUUID()).name("java").slug("java").build();
        Course course = Course.builder()
                .id(courseId)
                .title("old title")
                .ownerId(ownerId)
                .category(category)
                .visibility(CourseVisibility.PRIVATE)
                .build();

        UpdateCourseRequest updateCourseRequest = new UpdateCourseRequest(null, null, null);

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId, courseId))
                .thenReturn(Optional.of(course));
        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        CourseResponse result = courseService.updateCourse(ownerId, courseId, updateCourseRequest);

        //Assert
        Assertions.assertThat(result.title()).isEqualTo("old title");
        Assertions.assertThat(result.category().id()).isEqualTo(category.getId());
        Assertions.assertThat(result.visibility()).isEqualTo(CourseVisibility.PRIVATE);

        Mockito.verify(courseCategoryRepository, Mockito.never()).findById(Mockito.any());
    }

    @Test
    public void updateCourse_shouldThrowCourseNotFoundException_whenCourseDoesNotExistOrBelongsToAnotherOwner(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        UpdateCourseRequest updateCourseRequest = new UpdateCourseRequest(
                "new title",
                UUID.randomUUID(),
                CourseVisibility.PUBLIC
        );

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId, courseId))
                .thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.updateCourse(ownerId, courseId, updateCourseRequest));

        Mockito.verify(courseRepository).findCourseByOwnerIdAndCourseId(ownerId, courseId);
        Mockito.verify(courseCategoryRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(courseRepository, Mockito.never()).save(Mockito.any(Course.class));
        Mockito.verify(courseResponseMapper, Mockito.never()).toCourseResponse(Mockito.any(Course.class));
    }

    @Test
    public void updateCourse_shouldThrowCourseCategoryNotFoundException_whenCategoryDoesNotExist(){
        //Arrange
        String ownerId = "id";
        UUID courseId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Course course = Course.builder()
                .id(courseId)
                .title("old title")
                .ownerId(ownerId)
                .visibility(CourseVisibility.PRIVATE)
                .build();

        UpdateCourseRequest updateCourseRequest = new UpdateCourseRequest("new title", categoryId, null);

        Mockito.when(courseRepository.findCourseByOwnerIdAndCourseId(ownerId, courseId))
                .thenReturn(Optional.of(course));
        Mockito.when(courseCategoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(CourseCategoryNotFoundException.class,
                () -> courseService.updateCourse(ownerId, courseId, updateCourseRequest));

        Mockito.verify(courseCategoryRepository).findById(categoryId);
        Mockito.verify(courseRepository, Mockito.never()).save(Mockito.any(Course.class));
        Mockito.verify(courseResponseMapper, Mockito.never()).toCourseResponse(Mockito.any(Course.class));
    }

    @Test
    public void courseExists_shouldReturnTrue_whenCourseExists(){
        //Arrange
        UUID courseId = UUID.randomUUID();

        Mockito.when(courseRepository.existsById(courseId)).thenReturn(true);

        //Act
        boolean result = courseService.courseExists(courseId);

        //Assert
        Assertions.assertThat(result).isTrue();

        Mockito.verify(courseRepository).existsById(courseId);
    }

    @Test
    public void courseExists_shouldReturnFalse_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();

        Mockito.when(courseRepository.existsById(courseId)).thenReturn(false);

        //Act
        boolean result = courseService.courseExists(courseId);

        //Assert
        Assertions.assertThat(result).isFalse();

        Mockito.verify(courseRepository).existsById(courseId);
    }

    @Test
    public void createLesson_shouldReturnLessonResponse_whenCourseExists(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("Course Title").build();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Mockito.when(lessonRepository.save(Mockito.any(Lesson.class)))
                .thenAnswer(invocationOnMock -> {
                    Lesson lesson = invocationOnMock.getArgument(0);
                    lesson.setId(lessonId);
                    return lesson;
                });

        CreateLessonRequest createLessonRequest = new CreateLessonRequest("lesson title");

        //Act
        LessonResponse result = courseService.createLesson(courseId, createLessonRequest);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(lessonId);
        Assertions.assertThat(result.title()).isEqualTo(createLessonRequest.title());
        Assertions.assertThat(result.courseId()).isEqualTo(courseId);

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(lessonRepository).save(Mockito.any(Lesson.class));
        Mockito.verify(lessonResponseMapper).toLessonResponse(Mockito.any(Lesson.class));
    }

    @Test
    public void createLesson_shouldThrowCourseNotFoundException_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        CreateLessonRequest createLessonRequest = new CreateLessonRequest("lesson title");

        //Act & Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.createLesson(courseId, createLessonRequest));

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(lessonRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void getCourseLessonList_shouldReturnLessonResponseSummaryList_whenCourseExistsAndLessonsExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        UUID lessonId1 = UUID.randomUUID();
        UUID lessonId2 = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("Course Title").build();

        Lesson lesson1 = Lesson.builder().id(lessonId1).title("Lesson 1").course(course).build();
        Lesson lesson2 = Lesson.builder().id(lessonId2).title("Lesson 2").course(course).build();
        List<Lesson> lessonList = List.of(lesson1, lesson2);

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Mockito.when(lessonRepository.findByCourseId(courseId)).thenReturn(lessonList);

        //Act
        List<LessonSummaryResponse> result = courseService.getCourseLessonList(courseId);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).id()).isEqualTo(lessonId1);
        Assertions.assertThat(result.get(0).title()).isEqualTo("Lesson 1");
        Assertions.assertThat(result.get(0).courseId()).isEqualTo(courseId);
        Assertions.assertThat(result.get(1).id()).isEqualTo(lessonId2);
        Assertions.assertThat(result.get(1).title()).isEqualTo("Lesson 2");
        Assertions.assertThat(result.get(1).courseId()).isEqualTo(courseId);

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(lessonRepository).findByCourseId(courseId);
        Mockito.verify(lessonResponseMapper).toLessonResponseSummaryList(lessonList);
    }

    @Test
    public void getCourseLessonList_shouldReturnEmptyList_whenCourseExistsButNoLessonsExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("Course Title").build();
        List<Lesson> emptyList = List.of();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Mockito.when(lessonRepository.findByCourseId(courseId)).thenReturn(emptyList);

        //Act
        List<LessonSummaryResponse> result = courseService.getCourseLessonList(courseId);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEmpty();

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(lessonRepository).findByCourseId(courseId);
        Mockito.verify(lessonResponseMapper).toLessonResponseSummaryList(emptyList);
    }

    @Test
    public void getCourseLessonList_shouldThrowCourseNotFoundException_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.getCourseLessonList(courseId));

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(lessonRepository, Mockito.never()).findByCourseId(Mockito.any());
    }

}
