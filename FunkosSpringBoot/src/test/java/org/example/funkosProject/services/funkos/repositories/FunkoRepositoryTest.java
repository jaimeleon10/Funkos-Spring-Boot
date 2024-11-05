package org.example.funkosProject.services.funkos.repositories;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.models.TipoCategoria;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
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

    private static Categoria categoriaTest = new Categoria(UUID.randomUUID(), TipoCategoria.DISNEY, LocalDateTime.now(), LocalDateTime.now(), true);
    private static Funko funkoTest = new Funko(1L, "Mickey Mouse", 18.99, categoriaTest, LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach
    void setUp() {
        entityManager.merge(categoriaTest);
        entityManager.flush();
        entityManager.merge(funkoTest);
        entityManager.flush();
    }

    @Test
    void findAll() {
        var result = repository.findAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(funkoTest.getNombre(), result.getFirst().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), result.getFirst().getPrecio()),
                () -> assertEquals(funkoTest.getCategoria(), result.getFirst().getCategoria())
        );
    }

    @Test
    void findById() {
        var result = repository.findById(1L);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(funkoTest.getNombre(), result.get().getNombre()),
            () -> assertEquals(funkoTest.getPrecio(), result.get().getPrecio()),
            () -> assertEquals(funkoTest.getCategoria(), result.get().getCategoria())
        );
    }

    @Test
    void findByIdNotFound() {
        var result = repository.findById(999L);

        assertNull(result.orElse(null));
    }

    @Test
    void save() {
        var savedFunko = repository.save(funkoTest);

        assertAll(
                () -> assertNotNull(savedFunko.getId()),
                () -> assertEquals(funkoTest.getNombre(), savedFunko.getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), savedFunko.getPrecio()),
                () -> assertEquals(funkoTest.getCategoria(), savedFunko.getCategoria())
        );
    }

    @Test
    void deleteFunkoById() {
        repository.deleteById(1L);
        var result = repository.findById(1L);

        assertAll(
            () -> assertNull(result.orElse(null))
        );
    }

    @Test
    void deleteFunkoByIdNotFound() {
        repository.deleteById(999L);
        var result = repository.findById(999L);

        assertNull(result.orElse(null));
    }
}