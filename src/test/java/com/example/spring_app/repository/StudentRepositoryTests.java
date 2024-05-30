package com.example.spring_app.repository;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import com.example.spring_app.DTO.NameScoreDTO;
import com.example.spring_app.model.Department;
import com.example.spring_app.model.Student;
import com.example.spring_app.model.Subject;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class StudentRepositoryTests {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void should_return_all_student(){
        Student student = new Student("troc", 123);
        Student student1 = new Student("yen", 456);

        studentRepository.save(student);
        studentRepository.save(student1);
        List<Student> studentList = studentRepository.findAll();

        Assertions.assertThat(studentList).isNotNull();
        Assertions.assertThat(studentList.size()).isEqualTo(2);

    }

    @Test
    public void should_return_student_with_id(){
        Student student = new Student("troc", 123);

        studentRepository.save(student);

        Student record = studentRepository.findById(student.getId()).get();

        Assertions.assertThat(record).isNotNull();
    }
    @Test
    public void should_return_new_student(){
        Student student = new Student("troc", 123);

        studentRepository.save(student);

        Student record = studentRepository.findById(student.getId()).get();

        Assertions.assertThat(record).isNotNull();
        Assertions.assertThat(record.getName()).isEqualTo("troc");
        Assertions.assertThat(record.getScore()).isEqualTo(123);
    }
    @Test
    public void should_return_update_student(){
        Student student = new Student("troc", 123);

        studentRepository.save(student);

        Student record = studentRepository.findById(student.getId()).get();

        Assertions.assertThat(record).isNotNull();

        record.setName("tan tro doi");
        record.setScore(111);

        studentRepository.save(record);

        Assertions.assertThat(record).isNotNull();
        Assertions.assertThat(record.getName()).isEqualTo("tan tro doi");
        Assertions.assertThat(record.getScore()).isEqualTo(111);
    }
    @Test
    public void should_delete_student(){
        Student student = new Student("troc", 123);

        studentRepository.save(student);

        Student record = studentRepository.findById(student.getId()).get();

        Assertions.assertThat(record).isNotNull();
        
        studentRepository.delete(record);
        Student deletedStudent = studentRepository.findById(student.getId()).orElse(null);
        Assertions.assertThat(deletedStudent).isNull();
    }

    @Test
    public void should_return_list_student_by_name(){
        Student student = new Student("troc", 123);
        Student student1 = new Student("troc", 45);

        studentRepository.save(student);
        studentRepository.save(student1);

        List<Student> record = studentRepository.findStudentByName("troc");

        Assertions.assertThat(record.size()).isEqualTo(2);
    }

    @Test
    public void should_return_list_student_with_score(){
        Student student = new Student("troc", 123);
        Student student1 = new Student("troc", 45);

        studentRepository.save(student);
        studentRepository.save(student1);

        List<Student> record1 = studentRepository.findStudentByScore(123);
        List<Student> record2 = studentRepository.findStudentByScore(45);

        Assertions.assertThat(record1.size()).isEqualTo(1);
        Assertions.assertThat(record2.size()).isEqualTo(1);
        Assertions.assertThat(record1.get(0).getScore()).isEqualTo(123);
        Assertions.assertThat(record2.get(0).getScore()).isEqualTo(45);
    }

    @Test
    public void should_return_list_student_with_name_sorted_by_asc_or_desc(){
        Student student = new Student("troc", 123);
        Student student1 = new Student("yen", 45);
        Student student2 = new Student("sau", 145);

        studentRepository.save(student);
        studentRepository.save(student1);
        studentRepository.save(student2);

        List<String> record = studentRepository.sortStudentsByName(Sort.by(Sort.Direction.DESC, "name"));
        Assertions.assertThat(record).containsExactly("yen", "troc", "sau");
    }
    @Test
    public void should_return_list_student_with_score_in_range(){
        Student student = new Student("troc", 10);
        Student student1 = new Student("yen", 7);
        Student student2 = new Student("sau", 4);

        studentRepository.save(student);
        studentRepository.save(student1);
        studentRepository.save(student2);

        List<NameScoreDTO> record = studentRepository.findStudentByScoreInRange(7,10);
        Assertions.assertThat(record).hasSize(2);
        Assertions.assertThat(record).extracting(input -> input.name()).containsExactly("troc","yen");
        Assertions.assertThat(record).extracting(input -> input.score()).containsExactly(10, 7);

    }
    @Test
    public void should_return_list_student_name_match_pattern(){
        Student student = new Student("troc", 123);
        Student student1 = new Student("tan tro doi", 45);
        Student student2 = new Student("da lat", 145);

        studentRepository.save(student);
        studentRepository.save(student1);
        studentRepository.save(student2);

        List<Student> record = studentRepository.findByNamePattern("tr");
        Assertions.assertThat(record).hasSize(2);
        Assertions.assertThat(record).extracting(input -> input.getName()).containsExactly("troc", "tan tro doi");
    }
    
    @Test
    public void should_return_list_subject_enrolled_by_student(){
        
        //add subject to student and get all subjects
        Student student = new Student("troc", 123);
        Subject chemistry = new Subject("chemistry");
        Subject english = new Subject("english");
        Subject physics = new Subject("physics");
        student.setSubjects(Set.of(chemistry, english, physics));
        Set<Subject> subjects = student.getSubjects();
        Assertions.assertThat(subjects).hasSize(3);
        Assertions.assertThat(subjects).contains(chemistry, english, physics);
        
        //expect student has english subject
        Assertions.assertThat(subjects.contains(english));
        
        //do not add any subjects to student
        Student student1 = new Student("yen", 123);
        Set<Subject> subjects1 = student1.getSubjects();
        Assertions.assertThat(subjects1).isNull();
    }
    
    @Test
    public void should_return_department_of_student(){
        
        //add department to student
        Student student = new Student("troc", 123);
        Department department = new Department("Engineering");
    }
    
}
