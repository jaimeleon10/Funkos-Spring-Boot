package org.example.funkosProject.funko.repositories;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.funko.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FunkoRepositoryTest {

    @Autowired
    private FunkoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private static Categoria categoriaTest = new Categoria(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"), "CategoriaTest", LocalDateTime.now(), LocalDateTime.now(), true);
    private static Funko funkoTest = new Funko(1L, "FunkoTest", 10.00, categoriaTest, LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach
    void setUp() {
        categoriaTest = entityManager.merge(categoriaTest);
        entityManager.flush();
        funkoTest.setCategoria(categoriaTest);
        funkoTest = repository.saveAndFlush(funkoTest);
    }

    @Test
    void findByNombre() {
        var result = repository.findByNombre("FunkoTest");

        assertAll(
                () -> assertEquals(funkoTest.getNombre(), result.get().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), result.get().getPrecio()),
                () -> assertEquals(funkoTest.getCategoria(), result.get().getCategoria())
        );
    }

    @Test
    void findByNombreNotFound() {
        var result = repository.findByNombre("FunkoTestNotFound");

        assertNull(result.orElse(null));
    }


}