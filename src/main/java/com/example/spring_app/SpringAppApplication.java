package com.example.spring_app;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.spring_app.config.Datasource;
import com.example.spring_app.controller.RestController.StudentController;

@SpringBootApplication
public class SpringAppApplication {

	@Autowired
	Datasource datasource;

	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(StudentController.class);
		try {
			SpringApplication.run(SpringAppApplication.class, args);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

}
