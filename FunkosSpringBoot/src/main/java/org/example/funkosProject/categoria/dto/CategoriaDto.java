package org.example.funkosProject.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoriaDto {
    UUID id;
    @NotBlank(message = "El nombre no puede ser un campo vacio")
    String nombre;
    Boolean activado;
}
