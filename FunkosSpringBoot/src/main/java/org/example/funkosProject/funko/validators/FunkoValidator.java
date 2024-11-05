package org.example.funkosProject.funko.validators;

import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FunkoValidator {

    private final FunkoRepository funkoRepository; // Inyecta el repositorio de Funkos

    public FunkoValidator(FunkoRepository funkoRepository) {
        this.funkoRepository = funkoRepository;
    }

    /* TODO crear funcion que busque si el nombre existe

    public void validateUniqueName(String nombre) {
        if (funkoRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre del funko ya existe");
        }
    }*/
}
