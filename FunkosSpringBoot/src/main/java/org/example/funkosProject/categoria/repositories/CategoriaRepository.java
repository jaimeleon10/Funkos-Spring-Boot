package org.example.funkosProject.categoria.repositories;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.models.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    /*Optional<Categoria> findById(UUID id);*/
    Optional<Categoria> findByIdAndActivadoTrue(UUID id);
    Optional<Categoria> findByNombre(TipoCategoria nombre);
}