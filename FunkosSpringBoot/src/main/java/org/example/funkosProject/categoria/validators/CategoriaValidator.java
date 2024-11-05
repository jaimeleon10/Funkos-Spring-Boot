package org.example.funkosProject.categoria.validators;

import org.example.funkosProject.categoria.models.TipoCategoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaValidator {
    public boolean isNombreCategoriaValido(String nombre) {
        try {
            TipoCategoria.valueOf(nombre);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
