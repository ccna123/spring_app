package com.example.spring_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.spring_app.controller.RestController.StudentController;

@SpringBootApplication
public class SpringAppApplication {

	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(StudentController.class);
		try {
			SpringApplication.run(SpringAppApplication.class, args);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

}
