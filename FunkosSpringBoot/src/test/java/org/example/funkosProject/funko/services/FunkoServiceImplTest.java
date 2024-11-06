package org.example.funkosProject.funko.services;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.services.CategoriaService;
import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.mappers.FunkoMapper;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.example.funkosProject.funko.validators.FunkoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @Mock
    private FunkoValidator validator;

    @InjectMocks
    private FunkoServiceImpl service;

    private Funko funkoTest;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = new Categoria();
        categoriaTest.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        categoriaTest.setNombre("CategoriaTest");
        categoriaTest.setActivado(true);

        funkoTest = new Funko();
        funkoTest.setNombre("FunkoTest");
        funkoTest.setPrecio(10.00);
        funkoTest.setCategoria(categoriaTest);
    }

    @Test
    void getAll() {
        when(service.getAll()).thenReturn(List.of(funkoTest));

        var result = service.getAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertTrue(result.contains(funkoTest)),
                () -> assertEquals("FunkoTest", result.getFirst().getNombre()),
                () -> assertEquals(10.00, result.getFirst().getPrecio()),
                () -> assertEquals(categoriaTest, result.getFirst().getCategoria())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void getById() {
        when(validator.isIdValid("1")).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(funkoTest));

        var result = service.getById("1");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("FunkoTest", result.getNombre()),
                () -> assertEquals(10.00, result.getPrecio()),
                () -> assertEquals(categoriaTest, result.getCategoria())
        );

        verify(validator, times(1)).isIdValid("1");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getByIdNotValid() {
        when(validator.isIdValid("1a")).thenReturn(false);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.getById("1a")
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("El id no es valido. Debe ser de tipo Long", thrown.getReason());

        verify(validator, times(1)).isIdValid("1a");
    }

    @Test
    void getByIdNotFound() {
        when(validator.isIdValid("1")).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.getById("1")
        );

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("No existe el funko con id 1", thrown.getReason());

        verify(validator, times(1)).isIdValid("1");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getByNombre() {
    }

    @Test
    void save() {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("5790bdd4-8898-4c61-b547-bc26952dc2a3"));
        nuevaCategoria.setNombre("DISNEY");
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunko = new FunkoDto();
        nuevoFunko.setNombre("Mickey Mouse");
        nuevoFunko.setPrecio(7.95);
        nuevoFunko.setCategoria(nuevaCategoria.getNombre());

        Funko funkoMapped = new Funko();
        funkoMapped.setNombre(nuevoFunko.getNombre());
        funkoMapped.setPrecio(nuevoFunko.getPrecio());
        funkoMapped.setCategoria(nuevaCategoria);

        when(mapper.toFunko(nuevoFunko, nuevaCategoria)).thenReturn(funkoMapped);
        when(repository.save(funkoMapped)).thenReturn(funkoMapped);
        when(categoriaService.getByNombre("DISNEY")).thenReturn(nuevaCategoria);

        var result = service.save(nuevoFunko);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals("Mickey Mouse", result.getNombre()),
                () -> assertEquals(7.95, result.getPrecio()),
                () -> assertEquals(nuevaCategoria, result.getCategoria())
        );

        verify(repository, times(1)).save(funkoMapped);
        verify(mapper, times(1)).toFunko(nuevoFunko, nuevaCategoria);
        verify(categoriaService, times(1)).getByNombre(nuevaCategoria.getNombre());
    }

    @Test
    void update() {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("5790bdd4-8898-4c61-b547-bc26952dc2a3"));
        updatedCategoria.setNombre("SUPERHEROES");
        updatedCategoria.setActivado(true);

        FunkoDto updatedFunkoDto = new FunkoDto();
        updatedFunkoDto.setNombre("Superman");
        updatedFunkoDto.setPrecio(15.99);
        updatedFunkoDto.setCategoria(updatedCategoria.getNombre());

        Funko updatedFunko = new Funko();
        updatedFunko.setId(2L);
        updatedFunko.setNombre("Superman");
        updatedFunko.setPrecio(15.99);
        updatedFunko.setCategoria(updatedCategoria);

        when(repository.findById(2L)).thenReturn(Optional.of(updatedFunko));
        when(repository.save(updatedFunko)).thenReturn(updatedFunko);
        when(categoriaService.getByNombre("SUPERHEROES")).thenReturn(updatedCategoria);

        var result = service.update("1", updatedFunkoDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2L, result.getId()),
                () -> assertEquals("Superman", result.getNombre()),
                () -> assertEquals(15.99, result.getPrecio()),
                () -> assertEquals(updatedCategoria, result.getCategoria())
        );

        verify(repository, times(1)).findById(2L);
        verify(repository, times(1)).save(updatedFunko);
        verify(categoriaService, times(1)).getByNombre(updatedCategoria.getNombre());
    }

    @Test
    void delete() {
        when(repository.findById(1L)).thenReturn(Optional.of(funkoTest));

        var result = service.delete("1");

        assertAll(
                () -> assertEquals("FunkoTest", result.getNombre()),
                () -> assertEquals(10.00, result.getPrecio()),
                () -> assertEquals(categoriaTest, result.getCategoria())
        );

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}