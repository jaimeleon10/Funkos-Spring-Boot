package org.example.funkosProject.funko.validators;

import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.springframework.stereotype.Component;

@Component
public class FunkoValidator {

    private final FunkoRepository funkoRepository; // Inyecta el repositorio de Funkos

    public FunkoValidator(FunkoRepository funkoRepository) {
        this.funkoRepository = funkoRepository;
    }

    public boolean isNameUnique(String nombre) {
        return funkoRepository.findByNombre(nombre).isEmpty();
    }
}
