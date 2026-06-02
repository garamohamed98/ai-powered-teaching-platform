package com.mohamedgara.ai_teaching_platform.exercises.repositories;

import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
}
