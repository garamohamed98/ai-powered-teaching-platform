package com.mohamedgara.ai_teaching_platform.AI.exceptions;

public class AIRateLimitException extends RuntimeException{
    public AIRateLimitException(){super("Too many requests. pls try again later");}
}
