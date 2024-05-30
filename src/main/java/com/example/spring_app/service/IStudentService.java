package com.example.spring_app.service;

import java.util.List;

import com.example.spring_app.model.Student;
import com.example.spring_app.DTO.NameScoreDTO;

interface IStudentService extends ICrud<Student> {

    List<Student> findStudentByName(String name);

    List<Student> findStudentByScore(int score);
    
    Student addSubjectToStudent(int studentId, int subjectId);
    
    Student removeSubjectToStudent(int studentId, int subjectId);
    
    List<String> sortStudentsByName(String type);
    
    List<NameScoreDTO> findStudentByScoreInRange(int start, int end);

    List<Student> findStudentByNamePattern(String pattern);
}