package com.Fullstack.reactSpringBoot.models.userManagement;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MatriculeValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MatriculeConstraint {
    String message() default "Invalid matricule";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
