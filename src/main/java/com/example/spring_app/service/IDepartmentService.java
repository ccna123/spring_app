package com.example.spring_app.service;

import com.example.spring_app.model.Department;

public interface IDepartmentService extends ICrud<Department> {

    Department getDepartmentOfStudent(int studentId);
}
