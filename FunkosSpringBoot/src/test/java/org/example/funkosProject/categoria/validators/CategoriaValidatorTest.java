package org.example.funkosProject.categoria.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoriaValidatorTest {

    @InjectMocks
    private CategoriaValidator categoriaValidator;

    @Test
    void isIdValid() {
        String idValida = "4182d617-ec89-4fbc-be95-85e461778766";

        boolean result = categoriaValidator.isIdValid(idValida);

        assertTrue(result);
    }

    @Test
    void isIdInvalid() {
        String idInvalida = "4182d617-ec89-4f";

        boolean result = categoriaValidator.isIdValid(idInvalida);

        assertFalse(result);
    }
}