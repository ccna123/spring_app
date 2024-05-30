package com.example.spring_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spring_app.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("SELECT s.department FROM Student s WHERE s.id = :studentId")
    Department getDepartmentById(@Param("studentId") int studentId);

}
