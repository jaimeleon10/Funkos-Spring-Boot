package org.example.funkosProject.funko.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FunkoValidatorTest {

    @InjectMocks
    private FunkoValidator funkoValidator;

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