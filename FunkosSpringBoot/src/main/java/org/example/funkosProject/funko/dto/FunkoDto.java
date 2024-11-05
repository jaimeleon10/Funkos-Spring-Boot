package org.example.funkosProject.funko.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.funkosProject.categoria.models.TipoCategoria;

@Data
public class FunkoDto {
        @NotBlank(message = "El nombre no puede ser un campo vacio")
        String nombre;
        @Min(value = 0)
        @Max(value = 50)
        Double precio;
        @NotNull(message = "La categoria no puede un campo vacio")
        TipoCategoria categoria;
}