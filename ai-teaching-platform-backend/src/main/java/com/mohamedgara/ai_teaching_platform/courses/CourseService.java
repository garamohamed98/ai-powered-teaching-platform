package com.mohamedgara.ai_teaching_platform.courses;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.exceptions.ServiceNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseRequestMapper;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRequestMapper courseRequestMapper;
    private final CourseRepository courseRepository;
    private final CourseResponseMapper courseResponseMapper;

    public CourseResponse createCourse(CourseRequest courseRequest){
        Course course = courseRequestMapper.toCourse(courseRequest);
        Course savedCourse = courseRepository.save(course);
        return courseResponseMapper.toCourseResponse(savedCourse);
    }

    public List<CourseResponse> getCourseList(){
        List<Course> courseList = courseRepository.findAll();
        return courseResponseMapper.toCourseListResponse(courseList);
    }

    public CourseResponse getCourse(UUID id){
        Course course = courseRepository.findById(id).orElseThrow(()-> new ServiceNotFoundException());
        return courseResponseMapper.toCourseResponse(course);
    }

    @Transactional
    public void deleteCourse(UUID id){
        Course course = courseRepository.findById(id).orElseThrow(()-> new ServiceNotFoundException());
        courseRepository.delete(course);
    }

    @Transactional
    public CourseResponse updateCourseContent (UUID courseId, CourseContentUpdateRequest courseContentUpdateRequest){
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new ServiceNotFoundException());
        String courseContent = courseContentUpdateRequest.content();

        course.setContent(courseContent);
        Course updatedCourse = courseRepository.save(course);
        return courseResponseMapper.toCourseResponse(updatedCourse);
    }

}
