package org.example.funkosProject.categoria.mappers;

import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.models.TipoCategoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriaMapper {
    public Categoria toCategoria(CategoriaDto categoriaDto) {
        var categoria = new Categoria();
        categoria.setNombre(TipoCategoria.valueOf(categoriaDto.getNombre()));
        return categoria;
    }

    public Categoria toCategoriaUpdate(CategoriaDto categoriaDto, Categoria categoria){
        return new Categoria(
                categoria.getId(),
                categoriaDto.getNombre() != null ? TipoCategoria.valueOf(categoriaDto.getNombre()) : categoria.getNombre(),
                categoria.getCreatedAt(),
                LocalDateTime.now(),
                categoriaDto.getActivado() != null ? categoriaDto.getActivado() : categoria.getActivado()
        );
    }
}
