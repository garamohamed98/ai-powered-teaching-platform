package com.mohamedgara.ai_teaching_platform.courses.repository;


import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

}
