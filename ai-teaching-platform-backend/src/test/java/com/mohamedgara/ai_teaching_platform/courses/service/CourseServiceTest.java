package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateLessonRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonSummaryResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapperImpl;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapperImpl;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseTitleUpdateRequest;
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
    @Spy
    private final CourseResponseMapper courseResponseMapper = new CourseResponseMapperImpl();
    @Spy
    private final LessonResponseMapper lessonResponseMapper = new LessonResponseMapperImpl();
    @InjectMocks
    private CourseService courseService;

    @Test
    public void createCourse_shouldReturnCourseResponse_whenCourseRequestTitleFieldExists(){

        //Arrange
        UUID courseId = UUID.randomUUID();

        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> {
                    Course course = invocationOnMock.getArgument(0);
                    course.setId(courseId);
                    return course;
                });

        CreateCourseRequest createCourseRequest = new CreateCourseRequest(
                "title example"
        );

        //Act
        CourseResponse result = courseService.createCourse(createCourseRequest);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(courseId);
        Assertions.assertThat(result.title()).isEqualTo(createCourseRequest.title());


        Mockito.verify(courseRepository).save(Mockito.any(Course.class));
        Mockito.verify(courseResponseMapper).toCourseResponse(Mockito.any(Course.class));

    }

    @Test
    public void getCourseList_shouldReturnCourseResponseList_whenCoursesExist(){
        //Arrange
        UUID courseId1 = UUID.randomUUID();
        UUID courseId2 = UUID.randomUUID();

        Course course1 = Course.builder().id(courseId1).title("Course 1").build();
        Course course2 = Course.builder().id(courseId2).title("Course 2").build();

        List<Course> courseList = List.of(course1, course2);

        Mockito.when(courseRepository.findAll()).thenReturn(courseList);

        //Act
        List<CourseResponse> result = courseService.getCourseList();

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).id()).isEqualTo(courseId1);
        Assertions.assertThat(result.get(0).title()).isEqualTo("Course 1");
        Assertions.assertThat(result.get(1).id()).isEqualTo(courseId2);
        Assertions.assertThat(result.get(1).title()).isEqualTo("Course 2");

        Mockito.verify(courseRepository).findAll();
        Mockito.verify(courseResponseMapper).toCourseListResponse(courseList);
    }

    @Test
    public void getCourseList_shouldReturnEmptyList_whenNoCoursesExist(){
        //Arrange
        List<Course> emptyList = List.of();

        Mockito.when(courseRepository.findAll()).thenReturn(emptyList);

        //Act
        List<CourseResponse> result = courseService.getCourseList();

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEmpty();

        Mockito.verify(courseRepository).findAll();
        Mockito.verify(courseResponseMapper).toCourseListResponse(emptyList);
    }



    @Test
    public void deleteCourse_shouldDeleteCourse_whenCourseExists(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("title example").build();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        //Act
        courseService.deleteCourse(courseId);

        //Assert
        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(courseRepository).delete(course);
    }

    @Test
    public void deleteCourse_shouldThrowServiceNotFoundException_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.deleteCourse(courseId));

        Mockito.verify(courseRepository).findById(courseId);
    }

    @Test
    public void updateCourseTitle_shouldReturnUpdatedCourseResponse_whenCourseExists(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("title example").build();
        String newTitle = "new title";
        CourseTitleUpdateRequest updateRequest = new CourseTitleUpdateRequest(newTitle);

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        CourseResponse result = courseService.updateCourseTitle(courseId, updateRequest);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(courseId);
        Assertions.assertThat(result.title()).isEqualTo("new title");

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(courseRepository).save(course);
        Mockito.verify(courseResponseMapper).toCourseResponse(course);
    }

    @Test
    public void updateCourseTitle_shouldThrowCourseNotFoundException_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        CourseTitleUpdateRequest updateRequest = new CourseTitleUpdateRequest("new content");

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        //Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.updateCourseTitle(courseId,updateRequest));

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(courseRepository, Mockito.never()).save(Mockito.any());
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
