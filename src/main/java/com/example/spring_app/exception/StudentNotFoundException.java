package com.example.spring_app.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String mess) {
        super(mess);
    }
}
