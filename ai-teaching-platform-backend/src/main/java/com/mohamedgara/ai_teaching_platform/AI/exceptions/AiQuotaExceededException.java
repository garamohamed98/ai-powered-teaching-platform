package com.mohamedgara.ai_teaching_platform.AI.exceptions;

public class AiQuotaExceededException extends RuntimeException{
    public AiQuotaExceededException(){super("AI service quota exceeded. Please try again later.");}
}
