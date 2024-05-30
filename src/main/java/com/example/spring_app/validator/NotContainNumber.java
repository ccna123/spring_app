package com.example.spring_app.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotContainNumberValidator.class)
@Documented
public @interface NotContainNumber {
	String message();
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default{};
}
