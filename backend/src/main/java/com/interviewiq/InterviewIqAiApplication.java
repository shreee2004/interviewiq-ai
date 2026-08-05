package com.interviewiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InterviewIqAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewIqAiApplication.class, args);
	}

}
