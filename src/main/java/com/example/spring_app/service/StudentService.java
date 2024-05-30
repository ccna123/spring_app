package com.example.spring_app.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.spring_app.exception.StudentNotFoundException;
import com.example.spring_app.model.Student;
import com.example.spring_app.model.Subject;
import com.example.spring_app.repository.StudentRepository;
import com.example.spring_app.repository.SubjectRepository;
import com.example.spring_app.DTO.NameScoreDTO;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentService implements IStudentService {

    StudentRepository studentRepository;
    SubjectRepository subjectRepository;

    public StudentService(StudentRepository studentRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student getSingle(int id) {
        Optional<Student> studentContainer = studentRepository.findById(id);
        if (studentContainer.isPresent()) {
            return studentContainer.get();
        } else {
            throw new StudentNotFoundException("Not found");
        }
    }

    @Override
    public Student add(Student newStudent) {
        studentRepository.save(newStudent);
        return newStudent;
    }

    @Override
    public Student update(int id, Student updateStudent) {
        Optional<Student> studentContainer = studentRepository.findById(id);
        if (studentContainer.isPresent()) {
            Student currentStudent = studentContainer.get();
            currentStudent.setName(updateStudent.getName());
            currentStudent.setScore(updateStudent.getScore());
            studentRepository.save(currentStudent);
            return updateStudent;
        } else {
            throw new StudentNotFoundException("Student with id " + id + " not found");
        }
    }

    @Override
    public Student delete(int id) {
        Optional<Student> studentContainer = studentRepository.findById(id);
        if (studentContainer.isPresent()) {
            studentRepository.deleteById(id);
            return studentContainer.get();
        } else {
            throw new StudentNotFoundException("Student with id " + id + " not found");
        }
    }

    @Override
    public List<Student> findStudentByName(String name) {
        List<Student> students = studentRepository.findStudentByName(name);
        if (!students.isEmpty()) {
            return students;
        } else {
            throw new StudentNotFoundException("Student with name " + name + " not found");
        }
    }

    @Override
    public List<Student> findStudentByScore(int score) {
        List<Student> students = studentRepository.findStudentByScore(score);
        if (!students.isEmpty()) {
            return students;
        } else {
            throw new StudentNotFoundException("Student with score " + score + " not found");
        }
    }
    
    @Override
    public Student addSubjectToStudent(int studentId, int subjectId) {
    	Student student = studentRepository.findById(studentId).get();
    	Subject subject = subjectRepository.findById(subjectId).get();
    	
    	if (student != null && subject != null) {
    		if (student.getSubjects().contains(subject)) {
				throw new RuntimeException("Subject already enrolled");
			}
			student.getSubjects().add(subject);
			studentRepository.save(student);
			return student;
		} else {
			throw new RuntimeException("Student or subject not found");
		}
    }
    
    @Override
    public Student removeSubjectToStudent(int studentId, int subjectId) {
    	Student student = studentRepository.findById(studentId).get();
    	Subject subject = subjectRepository.findById(subjectId).get();
    	
    	if (student != null && subject != null) {
    		if (student.getSubjects().size() == 0) {
				throw new RuntimeException("There are no subject");
			}
			student.getSubjects().remove(subject);
			studentRepository.save(student);
			return student;
		} else {
			throw new RuntimeException("Student or subject not found");
		}
    }
    
    @Override
    public List<String> sortStudentsByName(String type) {
    	switch (type) {
		case "asc": {
			return studentRepository.sortStudentsByName(Sort.by(Sort.Direction.ASC, "name"));
		}
		case "desc":
			return studentRepository.sortStudentsByName(Sort.by(Sort.Direction.DESC, "name"));
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
    	
    }
    
    @Override
    public List<NameScoreDTO> findStudentByScoreInRange(int start, int end) {
        return studentRepository.findStudentByScoreInRange(start, end);
    }

}
