package com.example.spring_app.controller.RestController;

import org.springframework.web.bind.annotation.RestController;

import com.example.spring_app.exception.DepartmentNotFoundException;
import com.example.spring_app.model.Department;
import com.example.spring_app.response.ResponseHandler;
import com.example.spring_app.service.DepartmentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/department")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping(value = { "/get", "/get/{id}" })
    public ResponseEntity<Object> getDepDetail(@PathVariable(value = "id", required = false) Integer id) {
        try {
            if (id == null) {
                List<Department> records = departmentService.getAll();
                return ResponseHandler.ResponseBuilder("Success", HttpStatus.OK, records);
            } else {
                Department record = departmentService.getSingle(id);
                return ResponseHandler.ResponseBuilder("Success", HttpStatus.OK, record);
            }
        } catch (DepartmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addDep(@RequestBody Department newDep) {
        try {
            Department record = departmentService.add(newDep);
            return ResponseHandler.ResponseBuilder("Created successfully", HttpStatus.CREATED, record);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Object> updateDep(@PathVariable("id") Integer id, @RequestBody Department updateDep) {
        try {
            Department updatedSubject = departmentService.update(id, updateDep);
            return ResponseHandler.ResponseBuilder("Updated successfully", HttpStatus.CREATED, updatedSubject);
        } catch (DepartmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Object> deleteDep(@PathVariable("id") int id) {
        try {
            Department record = departmentService.delete(id);
            return ResponseHandler.ResponseBuilder("Deleted successfully", HttpStatus.CREATED, record);
        } catch (DepartmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<Object> getDepartmentOfStudent(@PathVariable("id") int id) {
        try {
            Department department = departmentService.getDepartmentOfStudent(id);
            return ResponseEntity.ok(department);
        } catch (DepartmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
