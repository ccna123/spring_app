package com.example.spring_app.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static ResponseEntity<Object> ResponseBuilder(
            String mess,
            HttpStatus httpStatus,
            Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", mess);
        response.put("HttpStatus", httpStatus);
        response.put("data", data);
        return new ResponseEntity<>(response, httpStatus);
    }
}
