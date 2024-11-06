package org.example.funkosProject.categoria.repositories;

import org.example.funkosProject.categoria.models.Categoria;
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
        categoriaTest.setNombre("CategoriaTest");
        categoriaTest.setActivado(true);

        entityManager.merge(categoriaTest);
        entityManager.flush();
    }

    @Test
    void findById() {
        var result = repository.findById(categoriaTest.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> {
                    if (result.isPresent()) {
                        assertEquals("CategoriaTest", result.get().getNombre());
                        assertTrue(result.get().getActivado());
                    }
                }
        );
    }

    @Test
    void findByIdNotFound() {
        var result = repository.findById(UUID.randomUUID());

        assertAll(
                () -> assertNotNull(result),
                () -> {
                    assertTrue(result.isEmpty());
                }
        );
    }

    @Test
    void findByIdAndActivadoTrue() {
        var result = repository.findByIdAndActivadoTrue(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));

        assertAll(
                () -> assertNotNull(result),
                () -> {
                    if (result.isPresent()) {
                        assertEquals("CategoriaTest", result.get().getNombre());
                        assertTrue(result.get().getActivado());
                    }
                }
        );
    }

    @Test
    void findByIdAndActivadoTrueNotFound() {
        var result = repository.findByIdAndActivadoTrue(UUID.randomUUID());

        assertAll(
                () -> assertNotNull(result),
                () -> {
                    assertTrue(result.isEmpty());
                }
        );
    }

    @Test
    void findByNombre() {
        var result = repository.findByNombre("CategoriaTest");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("CategoriaTest", result.get().getNombre()),
                () -> assertTrue(result.get().getActivado())
        );
    }

    @Test
    void findByNombreNotFound() {
        var result = repository.findByNombre("CategoriaTestNotFound");

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
    }
}