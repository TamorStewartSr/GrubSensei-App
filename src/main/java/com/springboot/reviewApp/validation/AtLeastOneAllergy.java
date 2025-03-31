package com.springboot.reviewApp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Define where this annotation can be applied (at class level)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneAllergyValidator.class)
public @interface AtLeastOneAllergy {
    String message() default "At least one allergy must be selected.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
