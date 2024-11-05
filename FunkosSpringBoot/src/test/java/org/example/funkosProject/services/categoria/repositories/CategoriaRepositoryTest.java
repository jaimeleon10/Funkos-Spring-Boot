package org.example.funkosProject.services.categoria.repositories;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = new Categoria();
        categoriaTest.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        categoriaTest.setNombre("DISNEY");
        categoriaTest.setActivado(true);
        entityManager.merge(categoriaTest);
    }

    @Test
    void findById() {
        var result = repository.findById(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.get().getNombre()),
                () -> assertTrue(result.get().getActivado())
        );
    }

    @Test
    void findByIdAndActivadoTrue() {
        var result = repository.findByIdAndActivadoTrue(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.get().getNombre()),
                () -> assertTrue(result.get().getActivado())
        );
    }

    @Test
    void findByNombre() {
        var result = repository.findByNombre("DISNEY");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.get().getNombre()),
                () -> assertTrue(result.get().getActivado())
        );
    }
}