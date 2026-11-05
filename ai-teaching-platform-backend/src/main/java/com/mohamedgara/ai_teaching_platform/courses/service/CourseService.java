package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.UpdateCourseRequest;
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
import com.mohamedgara.ai_teaching_platform.courses.exception.CourseNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseRequestMapper;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseCategoryRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
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
    private final LessonRepository lessonRepository;
    private final LessonResponseMapper lessonResponseMapper;
    private final CourseRequestMapper courseRequestMapper;
    private final CourseCategoryRepository courseCategoryRepository;

    @Transactional
    public CourseResponse createCourse(String authenticatedUserId,CreateCourseRequest createCourseRequest){

        Course course = courseRequestMapper.toCourse(createCourseRequest);

        CourseCategory category = courseCategoryRepository.findById(createCourseRequest.categoryId())
                .orElseThrow(()-> new CourseCategoryNotFoundException());

        course.setOwnerId(authenticatedUserId);
        course.setCategory(category);
        Course savedCourse = courseRepository.save(course);

        return courseResponseMapper.toCourseResponse(savedCourse);
    }

    public List<CourseResponse> getCourseList(String authenticatedUserId){
        List<Course> courseList = courseRepository.findCourseByOwnerId(authenticatedUserId);
        return courseResponseMapper.toCourseListResponse(courseList);
    }

    @Transactional
    public void deleteCourse(String authenticatedUserId, UUID id){
        Course course = courseRepository.findCourseByOwnerIdAndCourseId(authenticatedUserId,id).orElseThrow(()-> new CourseNotFoundException());
        courseRepository.delete(course);
    }

    @Transactional
    public CourseResponse updateCourse(String authenticatedUserId, UUID courseId, UpdateCourseRequest updateCourseRequest){
        Course course = courseRepository.findCourseByOwnerIdAndCourseId(
                authenticatedUserId,
                courseId
        ).orElseThrow(()-> new CourseNotFoundException());

        String courseTitle = updateCourseRequest.title();
        if(courseTitle != null){
            course.setTitle(courseTitle);
        }
        UUID courseCategoryId = updateCourseRequest.categoryId();
        if(courseCategoryId != null){
            CourseCategory courseCategory = courseCategoryRepository.findById(courseCategoryId)
                    .orElseThrow(()->new CourseCategoryNotFoundException());
            course.setCategory(courseCategory);
        }
        CourseVisibility courseVisibility = updateCourseRequest.visibility();
        if(courseVisibility != null){
            course.setVisibility(courseVisibility);
        }

        Course updatedCourse = courseRepository.save(course);
        return courseResponseMapper.toCourseResponse(updatedCourse);
    }

    public boolean courseExists(UUID courseId){
        return courseRepository.existsById(courseId);
    }

    public LessonResponse createLesson(UUID courseId, CreateLessonRequest createLessonRequest) {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseNotFoundException());
        String lessonTitle = createLessonRequest.title();

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(lessonTitle)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        return lessonResponseMapper.toLessonResponse(savedLesson);

    }

    @Transactional
    public List<LessonSummaryResponse> getCourseLessonList(String authenticatedUserId, UUID courseId) {
        Course course = courseRepository.findCourseByOwnerIdAndCourseId(
                authenticatedUserId,
                courseId
        ).orElseThrow(()-> new CourseNotFoundException());

        List<Lesson> lessonList = lessonRepository.findByCourseId(course.getId());

        return lessonResponseMapper.toLessonResponseSummaryList(lessonList);
    }
}
