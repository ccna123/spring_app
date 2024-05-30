package com.example.spring_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.spring_app.exception.SubjectNotFoundException;
import com.example.spring_app.model.Subject;
import com.example.spring_app.repository.SubjectRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SubjectService implements ICrud<Subject> {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject getSingle(int id) {
        Optional<Subject> subjectContainer = subjectRepository.findById(id);
        if (subjectContainer.isPresent()) {
            return subjectContainer.get();
        } else {
            throw new SubjectNotFoundException("Not found");
        }
    }

    @Override
    public Subject add(Subject subject) {
        subjectRepository.save(subject);
        return subject;
    }

    @Override
    public Subject update(int id, Subject updateSubject) {
        Optional<Subject> subjectContainer = subjectRepository.findById(id);
        if (subjectContainer.isPresent()) {
            Subject currenSubject = subjectContainer.get();
            currenSubject.setName(updateSubject.getName());
            subjectRepository.save(currenSubject);
            return updateSubject;
        } else {
            throw new SubjectNotFoundException("Not found");
        }
    }

    @Override
    public Subject delete(int id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isPresent()) {
            subjectRepository.deleteById(id);
            return subject.get();
        } else {
            throw new SubjectNotFoundException("Not found");
        }
    }

}
