package org.example.funkosProject.funko.validators;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.repositories.CategoriaRepository;
import org.example.funkosProject.categoria.validators.CategoriaValidator;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FunkoValidatorTest {
    @Mock
    private FunkoRepository repository;

    @InjectMocks
    private FunkoValidator funkoValidator;

    @Test
    void isNameUnique() {
        String nombre = "FunkoNuevo";

        when(repository.findByNombre(nombre)).thenReturn(Optional.empty());

        boolean result = funkoValidator.isNameUnique(nombre);

        assertTrue(result);

        verify(repository, times(1)).findByNombre(nombre);
    }

    @Test
    void isNameUniqueFalse() {
        String nombre = "FunkoExistente";

        when(repository.findByNombre(nombre)).thenReturn(Optional.of(new Funko()));

        boolean result = funkoValidator.isNameUnique(nombre);

        assertFalse(result);

        verify(repository, times(1)).findByNombre(nombre);
    }

    @Test
    public void isIdValid() {
        String id = "1";

        boolean result = funkoValidator.isIdValid(id);

        assertTrue(result);
    }

    @Test
    public void isIdInvalid() {
        String id = "1a";

        boolean result = funkoValidator.isIdValid(id);

        assertFalse(result);
    }
}