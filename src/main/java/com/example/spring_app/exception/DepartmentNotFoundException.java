package com.example.spring_app.exception;

public class DepartmentNotFoundException extends RuntimeException {

	public DepartmentNotFoundException(String mess) {
		super(mess);
	}
	
}
