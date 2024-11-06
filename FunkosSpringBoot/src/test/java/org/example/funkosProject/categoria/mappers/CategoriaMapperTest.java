package org.example.funkosProject.categoria.mappers;

import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.models.Categoria;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaMapperTest {

    private final CategoriaMapper mapper = new CategoriaMapper();

    @Test
    void toCategoria() {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setNombre("CATEGORIATEST");
        categoriaDto.setActivado(true);

        var res = mapper.toCategoria(categoriaDto);

        assertAll(
                () -> assertEquals(categoriaDto.getNombre(), res.getNombre()),
                () -> assertEquals(categoriaDto.getActivado(), res.getActivado())
        );
    }

    @Test
    void toCategoriaUpdate() {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setNombre("CATEGORIATEST");
        categoriaDto.setActivado(true);

        Categoria categoria = new Categoria(
                null,
                "CATEGORIATEST",
                LocalDateTime.now(),
                LocalDateTime.now(),
                categoriaDto.getActivado()
        );

        var res = mapper.toCategoriaUpdate(categoriaDto, categoria);

        assertAll(
                () -> assertNull(res.getId()),
                () -> assertEquals(categoriaDto.getId(), res.getId()),
                () -> assertEquals(categoriaDto.getNombre(), res.getNombre()),
                () -> assertEquals(categoriaDto.getActivado(), res.getActivado())
        );
    }

    @Test
    void toCategoriaUpdateNombreAndActivadoNull() {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setNombre(null);
        categoriaDto.setActivado(null);

        Categoria categoria = new Categoria(
                UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"),
                "CATEGORIATEST",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        var res = mapper.toCategoriaUpdate(categoriaDto, categoria);

        assertAll(
                () -> assertEquals(categoria.getId(), res.getId()),
                () -> assertEquals(categoria.getNombre(), res.getNombre()),
                () -> assertEquals(categoria.getActivado(), res.getActivado())
        );
    }
}