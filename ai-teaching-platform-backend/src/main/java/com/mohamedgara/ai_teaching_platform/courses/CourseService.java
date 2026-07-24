package com.mohamedgara.ai_teaching_platform.courses;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.exceptions.CourseNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseResponseMapper courseResponseMapper;

    public CourseResponse createCourse(CreateCourseRequest createCourseRequest){
        String courseTitle = createCourseRequest.title();
        Course course = Course.builder().title(courseTitle).build();
        Course savedCourse = courseRepository.save(course);
        return courseResponseMapper.toCourseResponse(savedCourse);
    }

    public List<CourseResponse> getCourseList(){
        List<Course> courseList = courseRepository.findAll();
        return courseResponseMapper.toCourseListResponse(courseList);
    }

    public CourseResponse getCourse(UUID id){
        Course course = courseRepository.findById(id).orElseThrow(()-> new CourseNotFoundException());
        return courseResponseMapper.toCourseResponse(course);
    }

    @Transactional
    public void deleteCourse(UUID id){
        Course course = courseRepository.findById(id).orElseThrow(()-> new CourseNotFoundException());
        courseRepository.delete(course);
    }

    @Transactional
    public CourseResponse updateCourseContent (UUID courseId, CourseContentUpdateRequest courseContentUpdateRequest){
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseNotFoundException());
        String courseContent = courseContentUpdateRequest.content();

        course.setContent(courseContent);
        Course updatedCourse = courseRepository.save(course);
        return courseResponseMapper.toCourseResponse(updatedCourse);
    }

    public boolean courseExists(UUID courseId){
        return courseRepository.existsById(courseId);
    }

}
