package com.example.spring_app.exception;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException(String mess) {
        super(mess);
    }
}
