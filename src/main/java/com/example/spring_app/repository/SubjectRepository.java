package com.example.spring_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spring_app.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

}
