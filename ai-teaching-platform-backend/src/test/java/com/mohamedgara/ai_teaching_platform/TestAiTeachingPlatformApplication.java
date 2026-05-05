package com.mohamedgara.ai_teaching_platform;

import org.springframework.boot.SpringApplication;

public class TestAiTeachingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(AiTeachingPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
