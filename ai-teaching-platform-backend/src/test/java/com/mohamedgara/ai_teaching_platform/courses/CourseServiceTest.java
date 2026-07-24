package com.mohamedgara.ai_teaching_platform.courses;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapperImpl;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.exceptions.CourseNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Spy
    private final CourseResponseMapper courseResponseMapper = new CourseResponseMapperImpl();
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
        Assertions.assertThat(result.content()).isNull();


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
    public void getCourse_shouldReturnCourseResponse_whenCourseExists(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("title example").build();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        //Act
        CourseResponse result = courseService.getCourse(courseId);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(courseId);
        Assertions.assertThat(result.title()).isEqualTo("title example");

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(courseResponseMapper).toCourseResponse(course);
    }

    @Test
    public void getCourse_shouldThrowServiceNotFoundException_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.getCourse(courseId));

        Mockito.verify(courseRepository).findById(courseId);
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
    public void updateCourseContent_shouldReturnUpdatedCourseResponse_whenCourseExists(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder().id(courseId).title("title example").build();
        String newContent = "new content";
        CourseContentUpdateRequest updateRequest = new CourseContentUpdateRequest(newContent);

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Mockito.when(courseRepository.save(Mockito.any(Course.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        CourseResponse result = courseService.updateCourseContent(courseId, updateRequest);

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(courseId);
        Assertions.assertThat(result.title()).isEqualTo("title example");
        Assertions.assertThat(result.content()).isEqualTo(newContent);

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(courseRepository).save(course);
        Mockito.verify(courseResponseMapper).toCourseResponse(course);
    }

    @Test
    public void updateCourseContent_shouldThrowCourseNotFoundException_whenCourseDoesNotExist(){
        //Arrange
        UUID courseId = UUID.randomUUID();
        CourseContentUpdateRequest updateRequest = new CourseContentUpdateRequest("new content");

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        //Assert
        assertThrows(CourseNotFoundException.class,
                () -> courseService.updateCourseContent(courseId,updateRequest));

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

}
