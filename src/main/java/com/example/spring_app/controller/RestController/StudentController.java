package com.example.spring_app.controller.RestController;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.spring_app.exception.StudentNotFoundException;
import com.example.spring_app.model.Student;
import com.example.spring_app.response.ResponseHandler;
import com.example.spring_app.service.StudentService;
import com.example.spring_app.DTO.NameScoreDTO;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/student/")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/health")
    public String health() {
        return "healthy";
    }

    @GetMapping(value = { "/get", "/get/{id}" })
    public ResponseEntity<Object> getStudentDetail(@PathVariable(value = "id", required = false) Integer id) {
        try {

            if (id != null && (id < 0 || !id.toString().matches("\\d+"))) {
                throw new BadRequestException("Invalid Id");
            }

            if (id == null) {
                List<Student> records = studentService.getAll();
                return ResponseHandler.ResponseBuilder("Success", HttpStatus.OK, records);
            } else {
                Student record = studentService.getSingle(id);
                return ResponseHandler.ResponseBuilder("Success", HttpStatus.OK, record);
            }
        } catch (StudentNotFoundException e) {
            logger.warn("StudentNotFoundException", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(BadRequestException e){
            logger.warn("BadRequestException", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } 
        catch (Exception e) {
            logger.error("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/find/name/{name}")
    public ResponseEntity<Object> findByName(@PathVariable("name") String name) {
        try {
            List<Student> students = studentService.findStudentByName(name);
            return ResponseEntity.ok(students);
        } catch (StudentNotFoundException e) {
            logger.warn("StudentNotFoundException", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/find/score/{score}")
    public ResponseEntity<Object> findByScore(@PathVariable("score") int score) {
        try {
            List<Student> students = studentService.findStudentByScore(score);
            return ResponseEntity.ok(students);
        } catch (StudentNotFoundException e) {
            logger.warn("StudentNotFoundException", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        
    }
    
    @PostMapping("/add")
    public ResponseEntity<Object> postMethodName(@Valid @RequestBody Student newStudent) {
        try {
            Student record = studentService.add(newStudent);
            return ResponseHandler.ResponseBuilder("Add student successfully", HttpStatus.CREATED, record);
        } catch (Exception e) {
            logger.error("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateStudent(@PathVariable("id") Integer id, @RequestBody Student student) {
        try {
            Student record = studentService.update(id, student);
            return ResponseHandler.ResponseBuilder("Updated successfully", HttpStatus.CREATED, record);
        } catch (StudentNotFoundException e) {
            logger.warn("StudentNotFoundException", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteStudent(@PathVariable("id") Integer id) {
        try {
            Student record = studentService.delete(id);
            return ResponseHandler.ResponseBuilder("Deleted successfully", HttpStatus.OK, record);
        } catch (StudentNotFoundException e) {
            logger.warn("StudentNotFoundException", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @PostMapping("/enroll/{studentId}/{subjectId}")
    public ResponseEntity<Object> addSubjectToStudent(
        @PathVariable("studentId") int studentId,
        @PathVariable("subjectId") int subjectId) {
            try {
                Student enroll = studentService.addSubjectToStudent(studentId, subjectId);
                return ResponseHandler.ResponseBuilder("Enroll successfully", HttpStatus.OK, enroll);
            } catch (StudentNotFoundException e) {
                logger.warn("StudentNotFoundException", e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                logger.error("Exception", e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }
        
        @PostMapping("/cancel/{studentId}/{subjectId}")
        public ResponseEntity<Object> removeSubjectToStudent(
            @PathVariable("studentId") int studentId,
            @PathVariable("subjectId") int subjectId) {
                try {
                    Student enroll = studentService.removeSubjectToStudent(studentId, subjectId);
                    return ResponseHandler.ResponseBuilder("Cancel subject successfully", HttpStatus.OK, enroll);
                } catch (StudentNotFoundException e) {
                    logger.warn("StudentNotFoundException", e);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                } catch (Exception e) {
                    logger.error("Exception", e);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
            }
            
            @GetMapping("/sort/{type}")
            public ResponseEntity<Object> sortStudentsByName(@PathVariable("type") String type) {
                try {
                    List<String> records = studentService.sortStudentsByName(type);
                    return ResponseEntity.ok(records);
                } catch (Exception e) {
                    logger.error("Exception", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                }
            }
            
            @GetMapping("/score/{start}/{end}")
            public ResponseEntity<Object> findStudentByScoreInRange(
                @PathVariable("start") int start,
                @PathVariable("end") int end) {
                    try {
                        List<NameScoreDTO> records = studentService.findStudentByScoreInRange(start, end);
                        return ResponseEntity.ok(records);
                    } catch (Exception e) {
                        logger.error("Exception", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                    }
                }
                
                @GetMapping("/name/{pattern}")
                public ResponseEntity<Object> findStudentByNamePattern(@PathVariable("pattern") String pattern){
                    try {
                        List<Student> records = studentService.findStudentByNamePattern(pattern);
                        return ResponseEntity.ok(records);
                    } catch (StudentNotFoundException e) {
                        logger.warn("StudentNotFoundException", e);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                    }catch(Exception e){
                        logger.error("Exception", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                    }
                }
                
            }
            