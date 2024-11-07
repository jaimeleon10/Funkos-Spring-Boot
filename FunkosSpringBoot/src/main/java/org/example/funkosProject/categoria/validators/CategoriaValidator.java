package org.example.funkosProject.categoria.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.example.funkosProject.categoria.repositories.CategoriaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CategoriaValidator {
    public boolean isIdValid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
