package com.interviewiq;

import org.springframework.boot.SpringApplication;

public class TestInterviewIqAiApplication {

	public static void main(String[] args) {
		SpringApplication.from(InterviewIqAiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
