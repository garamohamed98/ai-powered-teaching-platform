package com.mohamedgara.ai_teaching_platform.exercises.exceptions;

public class ExerciseAttemptNotFoundException extends RuntimeException{
    public ExerciseAttemptNotFoundException(){
        super("Exercise attempt not found");
    }
}
