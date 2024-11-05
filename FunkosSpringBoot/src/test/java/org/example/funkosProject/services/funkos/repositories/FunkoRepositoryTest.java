package org.example.funkosProject.services.funkos.repositories;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FunkoRepositoryTest {

    @Autowired
    private FunkoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Categoria categoriaTest = new Categoria(UUID.fromString("3e678c5a-4de3-42d1-ab6a-833fa06befcc"), "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);
    private static final Funko funkoTest = new Funko(1L, "Mickey Mouse", 18.99, categoriaTest, LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach
    void setUp() {
        entityManager.persistAndFlush(funkoTest);
        entityManager.persistAndFlush(categoriaTest);
    }

    @Test
    void findAll() {
        List<Funko> funkos = repository.findAll();

        assertAll("findAll",
                () -> assertNotNull(funkos),
                () -> assertFalse(funkos.isEmpty()),
                () -> assertEquals(1, funkos.size())
        );
    }

    @Test
    void findById() {
        var result = repository.findById(1L);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(funkoTest.getId(), result.get().getId()),
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