package com.example.spring_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.spring_app.exception.DepartmentNotFoundException;
import com.example.spring_app.exception.SubjectNotFoundException;
import com.example.spring_app.model.Department;
import com.example.spring_app.repository.DepartmentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DepartmentService implements IDepartmentService {

	private DepartmentRepository departmentRepository;

	public DepartmentService(DepartmentRepository departmentRepository) {
		this.departmentRepository = departmentRepository;
	}

	@Override
	public List<Department> getAll() {
		return departmentRepository.findAll();
	}

	@Override
	public Department getSingle(int id) {
		Optional<Department> departmentContainer = departmentRepository.findById(id);
		if (departmentContainer.isPresent()) {
			return departmentContainer.get();
		} else {
			throw new DepartmentNotFoundException("Not found");
		}
	}

	@Override
	public Department add(Department dep) {
		departmentRepository.save(dep);
		return dep;
	}

	@Override
	public Department update(int id, Department updateDep) {
		Optional<Department> depContainer = departmentRepository.findById(id);
		if (depContainer.isPresent()) {
			Department currenSubject = depContainer.get();
			currenSubject.setName(updateDep.getName());
			departmentRepository.save(currenSubject);
			return updateDep;
		} else {
			throw new DepartmentNotFoundException("Not found");
		}
	}

	@Override
	public Department delete(int id) {
		Optional<Department> dep = departmentRepository.findById(id);
		if (dep.isPresent()) {
			departmentRepository.deleteById(id);
			return dep.get();
		} else {
			throw new SubjectNotFoundException("Not found");
		}
	}

	@Override
	public Department getDepartmentOfStudent(int studentId) {
		return departmentRepository.getDepartmentById(studentId);
	}

}
