package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.exception.CourseNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
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

    @Transactional
    public void deleteCourse(UUID id){
        Course course = courseRepository.findById(id).orElseThrow(()-> new CourseNotFoundException());
        courseRepository.delete(course);
    }

    @Transactional
    public CourseResponse updateCourseTitle (UUID courseId, CourseTitleUpdateRequest courseTitleUpdateRequest){
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseNotFoundException());
        String courseTitle = courseTitleUpdateRequest.title();

        course.setTitle(courseTitle);
        Course updatedCourse = courseRepository.save(course);
        return courseResponseMapper.toCourseResponse(updatedCourse);
    }

    public boolean courseExists(UUID courseId){
        return courseRepository.existsById(courseId);
    }

}
