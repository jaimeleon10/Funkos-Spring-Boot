package org.example.funkosProject.services.funkos.services;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.models.TipoCategoria;
import org.example.funkosProject.categoria.services.CategoriaService;
import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.mappers.FunkoMapper;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.example.funkosProject.funko.services.FunkoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {
    @Mock
    private FunkoRepository repository;

    @Mock
    private FunkoMapper mapper;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private FunkoServiceImpl service;

    private Funko funkoTest;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = new Categoria();
        categoriaTest.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        categoriaTest.setNombre(TipoCategoria.DISNEY);
        categoriaTest.setActivado(true);

        funkoTest = new Funko();
        funkoTest.setNombre("Mickey Mouse");
        funkoTest.setPrecio(18.99);
        funkoTest.setCategoria(categoriaTest);
    }

    @Test
    void getAll() {
        when(service.getAll()).thenReturn(List.of(funkoTest));

        var result = service.getAll();

        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertTrue(result.contains(funkoTest)),
            () -> assertEquals("Mickey Mouse", result.getFirst().getNombre()),
            () -> assertEquals(18.99, result.getFirst().getPrecio()),
            () -> assertEquals(categoriaTest, result.getFirst().getCategoria())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void getById() {
        when(repository.findById(1L)).thenReturn(Optional.of(funkoTest));

        var result = service.getById(1L);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals("Mickey Mouse", result.getNombre()),
            () -> assertEquals(18.99, result.getPrecio()),
            () -> assertEquals(categoriaTest, result.getCategoria())
        );

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void save() {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778777"));
        nuevaCategoria.setNombre(TipoCategoria.PELICULA);
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunko = new FunkoDto();
        nuevoFunko.setNombre("Pluto");
        nuevoFunko.setPrecio(15.99);
        nuevoFunko.setCategoria(nuevaCategoria.getNombre());

        Funko funkoMapped = new Funko();
        funkoMapped.setNombre(nuevoFunko.getNombre());
        funkoMapped.setPrecio(nuevoFunko.getPrecio());
        funkoMapped.setCategoria(nuevaCategoria);

        when(mapper.toFunko(nuevoFunko, nuevaCategoria)).thenReturn(funkoMapped);
        when(repository.save(funkoMapped)).thenReturn(funkoMapped);
        when(categoriaService.getByNombre(TipoCategoria.PELICULA)).thenReturn(nuevaCategoria);

        var result = service.save(nuevoFunko);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals("Pluto", result.getNombre()),
                () -> assertEquals(15.99, result.getPrecio()),
                () -> assertEquals(nuevaCategoria, result.getCategoria())
        );

        verify(repository, times(1)).save(funkoMapped);
        verify(mapper, times(1)).toFunko(nuevoFunko, nuevaCategoria);
        verify(categoriaService, times(1)).getByNombre(nuevaCategoria.getNombre());
    }

    @Test
    void update() {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778788"));
        updatedCategoria.setNombre(TipoCategoria.SUPERHEROES);
        updatedCategoria.setActivado(true);

        Funko updatedFunko = new Funko();
        updatedFunko.setId(2L);
        updatedFunko.setNombre("SpiderMan");
        updatedFunko.setPrecio(12.99);
        updatedFunko.setCategoria(updatedCategoria);

        when(repository.findById(2L)).thenReturn(Optional.of(updatedFunko));
        when(repository.save(updatedFunko)).thenReturn(updatedFunko);

        var result = service.update(2L, updatedFunko);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(2L, result.getId()),
            () -> assertEquals("SpiderMan", result.getNombre()),
            () -> assertEquals(12.99, result.getPrecio()),
            () -> assertEquals(updatedCategoria, result.getCategoria())
        );

        verify(repository, times(1)).findById(2L);
        verify(repository, times(1)).save(updatedFunko);
    }

    @Test
    void delete() {
        when(repository.findById(1L)).thenReturn(Optional.of(funkoTest));

        var result = service.delete(1L);

        assertAll(
                () -> assertEquals("Mickey Mouse", result.getNombre()),
                () -> assertEquals(18.99, result.getPrecio()),
                () -> assertEquals(categoriaTest, result.getCategoria())
        );

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteFunkoById(1L);
    }
}