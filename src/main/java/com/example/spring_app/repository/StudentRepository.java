package com.example.spring_app.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.spring_app.model.Student;
import com.example.spring_app.DTO.NameScoreDTO;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findStudentByName(String name);

    List<Student> findStudentByScore(int score);
    
    @Query("SELECT s.name FROM Student s")
    List<String> sortStudentsByName(Sort sort);
    
    @Query("SELECT NEW com.example.DTO.NameScoreDTO(s.name, s.score) FROM Student s WHERE s.score BETWEEN :start AND :end")
    List<NameScoreDTO> findStudentByScoreInRange(@Param("start") int start, @Param("end") int end);

}
