package com.example.spring_app.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotContainNumberValidator implements ConstraintValidator<NotContainNumber, String>{

	@Override
	public void initialize(NotContainNumber constraintAnnotation) {
		// TODO Auto-generated method stub
		ConstraintValidator.super.initialize(constraintAnnotation);
	}
	
	@Override
	public boolean isValid(String input, ConstraintValidatorContext context) {
		return input == null || !input.matches(".*\\\\d.*");
	}
	

}
