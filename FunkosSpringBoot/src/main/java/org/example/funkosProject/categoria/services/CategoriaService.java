package org.example.funkosProject.categoria.services;

import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.models.Categoria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoriaService {
    List<Categoria> getAll();
    Categoria getById(UUID id);
    Categoria getByNombre(String nombre);
    Categoria save(CategoriaDto categoriaDto);
    Categoria update(UUID id, CategoriaDto categoriaDto);
    Categoria delete(UUID id, CategoriaDto categoriaDto);
}