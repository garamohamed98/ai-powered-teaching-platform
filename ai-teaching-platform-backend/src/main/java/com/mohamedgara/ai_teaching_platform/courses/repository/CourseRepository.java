package com.mohamedgara.ai_teaching_platform.courses.repository;


import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("""
            SELECT c
            FROM Course c
            WHERE c.ownerId =:ownerId
            """)
    List<Course> findCourseByOwnerId(@Param("ownerId") String ownerId);

}
