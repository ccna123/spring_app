package com.example.spring_app.controller.RestController;

import org.springframework.web.bind.annotation.RestController;

import com.example.spring_app.exception.SubjectNotFoundException;
import com.example.spring_app.model.Subject;
import com.example.spring_app.response.ResponseHandler;
import com.example.spring_app.service.SubjectService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/subject")
@SecurityRequirement(name = "bearerAuth")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping(value = { "/get", "/get/{id}" })
    public ResponseEntity<Object> getSubjectDetail(@PathVariable(value = "id", required = false) Integer id) {
        try {
            if (id == null) {
                List<Subject> records = subjectService.getAll();
                return ResponseHandler.ResponseBuilder("Success", HttpStatus.OK, records);
            } else {
                Subject record = subjectService.getSingle(id);
                return ResponseHandler.ResponseBuilder("Success", HttpStatus.OK, record);
            }
        } catch (SubjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/add")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<Object> addSubject(@RequestBody Subject newSubject) {
        try {
            Subject record = subjectService.add(newSubject);
            return ResponseHandler.ResponseBuilder("Created successfully", HttpStatus.CREATED, record);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Object> updateSubject(@PathVariable("id") Integer id, @RequestBody Subject updateSubject) {
        try {
            Subject updatedSubject = subjectService.update(id, updateSubject);
            return ResponseHandler.ResponseBuilder("Updated successfully", HttpStatus.CREATED, updatedSubject);
        } catch (SubjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Object> deleteSubject(@PathVariable("id") int id) {
        try {
            Subject record = subjectService.delete(id);
            return ResponseHandler.ResponseBuilder("Deleted successfully", HttpStatus.CREATED, record);
        } catch (SubjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
