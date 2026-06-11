package com.mohamedgara.ai_teaching_platform;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class config {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
