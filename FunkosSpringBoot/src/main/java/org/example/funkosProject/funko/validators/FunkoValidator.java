package org.example.funkosProject.funko.validators;

import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FunkoValidator {
    public boolean isIdValid(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
