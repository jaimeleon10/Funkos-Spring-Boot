package org.example.funkosProject.categoria.mappers;

import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.models.Categoria;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
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
}