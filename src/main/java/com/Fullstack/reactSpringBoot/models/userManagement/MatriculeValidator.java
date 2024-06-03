package com.Fullstack.reactSpringBoot.models.userManagement;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class MatriculeValidator implements ConstraintValidator<MatriculeConstraint, Integer> {

    @Override
    public void initialize(MatriculeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer matricule, ConstraintValidatorContext context) {
        // Vérifier que le matricule est entre 1000 et 9999
        return matricule != null && matricule >= 1000 && matricule <= 9999;
    }
}

