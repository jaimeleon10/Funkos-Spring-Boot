package org.example.funkosProject.funko.services;

import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.services.CategoriaService;
import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.mappers.FunkoMapper;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.example.funkosProject.funko.validators.FunkoValidator;
import org.example.funkosProject.notifications.config.WebSocketConfig;
import org.example.funkosProject.notifications.config.WebSocketHandler;
import org.example.funkosProject.notifications.mappers.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    @Mock
    private WebSocketConfig webSocketConfig;

    WebSocketHandler webSocketHandler = mock(WebSocketHandler.class);

    @Mock
    private NotificationMapper notificacionMapper;

    @Captor
    private ArgumentCaptor<Funko> funkoCaptor;

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

        service.setWebSocketHandler(webSocketHandler);
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
    void getByNombreFunkoExists() {
        Funko funkoExistente = new Funko();
        funkoExistente.setId(1L);
        funkoExistente.setNombre("FunkoTest");
        funkoExistente.setPrecio(10.00);

        when(repository.findByNombre("FunkoTest")).thenReturn(Optional.of(funkoExistente));

        Funko result = service.getByNombre("FunkoTest");

        assertNotNull(result);
        assertEquals("FunkoTest", result.getNombre());
        assertEquals(10.00, result.getPrecio());

        verify(repository, times(1)).findByNombre("FunkoTest");
    }

    @Test
    void getByNombreFunkoNotFound() {
        when(repository.findByNombre("FunkoTestNotFound")).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.getByNombre("FunkoTestNotFound")
        );

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("El funko llamado FunkoTestNotFound no existe", thrown.getReason());

        verify(repository, times(1)).findByNombre("FunkoTestNotFound");
    }

    @Test
    void save() throws IOException {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        nuevaCategoria.setNombre("CategoriaTest");
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunkoDto = new FunkoDto();
        nuevoFunkoDto.setNombre("FunkoTest");
        nuevoFunkoDto.setPrecio(10.00);
        nuevoFunkoDto.setCategoria(nuevaCategoria.getNombre());

        Funko nuevoFunko = new Funko();
        nuevoFunko.setNombre(nuevoFunkoDto.getNombre());
        nuevoFunko.setPrecio(nuevoFunkoDto.getPrecio());
        nuevoFunko.setCategoria(nuevaCategoria);

        when(categoriaService.getByNombre(nuevaCategoria.getNombre())).thenReturn(nuevaCategoria);
        when(validator.isNameUnique(nuevoFunkoDto.getNombre())).thenReturn(true);
        when(mapper.toFunko(nuevoFunkoDto, nuevaCategoria)).thenReturn(nuevoFunko);
        when(repository.save(nuevoFunko)).thenReturn(nuevoFunko);
        doNothing().when(webSocketHandler).sendMessage(any());

        var result = service.save(nuevoFunkoDto);

        assertAll(
                () -> assertEquals("FunkoTest", result.getNombre()),
                () -> assertEquals(10.00, result.getPrecio()),
                () -> assertEquals(nuevaCategoria, result.getCategoria())
        );

        verify(repository, times(1)).save(nuevoFunko);
        verify(mapper, times(1)).toFunko(nuevoFunkoDto, nuevaCategoria);
        verify(categoriaService, times(1)).getByNombre(nuevaCategoria.getNombre());
    }

    @Test
    void saveCategoriaNameNotFound() {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        nuevaCategoria.setNombre("CategoriaTest");
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunkoDto = new FunkoDto();
        nuevoFunkoDto.setNombre("FunkoTest");
        nuevoFunkoDto.setPrecio(10.00);
        nuevoFunkoDto.setCategoria(nuevaCategoria.getNombre());

        when(categoriaService.getByNombre(nuevaCategoria.getNombre())).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoria CategoriaTest no existe")
        );

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.save(nuevoFunkoDto)
        );

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("La categoria CategoriaTest no existe", thrown.getReason());

        verify(categoriaService, times(1)).getByNombre(nuevaCategoria.getNombre());
    }

    @Test
    void saveFunkoNameNotUnique() {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        nuevaCategoria.setNombre("CategoriaTest");
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunkoDto = new FunkoDto();
        nuevoFunkoDto.setNombre("FunkoTest");
        nuevoFunkoDto.setPrecio(10.00);
        nuevoFunkoDto.setCategoria(nuevaCategoria.getNombre());

        when(categoriaService.getByNombre(nuevaCategoria.getNombre())).thenReturn(nuevaCategoria);
        when(validator.isNameUnique(nuevoFunkoDto.getNombre())).thenReturn(false);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.save(nuevoFunkoDto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("El nombre del funko ya existe", thrown.getReason());

        verify(categoriaService, times(1)).getByNombre(nuevaCategoria.getNombre());
        verify(validator, times(1)).isNameUnique(nuevoFunkoDto.getNombre());
    }

    @Test
    void update() throws IOException {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        updatedCategoria.setNombre("CategoriaTest");
        updatedCategoria.setActivado(true);

        FunkoDto updatedFunkoDto = new FunkoDto();
        updatedFunkoDto.setNombre("FunkoTest");
        updatedFunkoDto.setPrecio(10.00);
        updatedFunkoDto.setCategoria(updatedCategoria.getNombre());

        Funko updatedFunko = new Funko();
        updatedFunko.setId(2L);
        updatedFunko.setNombre("FunkoTest");
        updatedFunko.setPrecio(10.00);
        updatedFunko.setCategoria(updatedCategoria);

        when(validator.isIdValid("2")).thenReturn(true);
        when(repository.findById(2L)).thenReturn(Optional.of(updatedFunko));
        when(validator.isNameUnique(updatedFunkoDto.getNombre())).thenReturn(true);
        when(categoriaService.getByNombre(updatedFunkoDto.getCategoria())).thenReturn(updatedCategoria);
        when(repository.save(updatedFunko)).thenReturn(updatedFunko);
        doNothing().when(webSocketHandler).sendMessage(any());

        var result = service.update("2", updatedFunkoDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedFunko.getId(), result.getId()),
                () -> assertEquals(updatedFunko.getNombre(), result.getNombre()),
                () -> assertEquals(updatedFunko.getPrecio(), result.getPrecio()),
                () -> assertEquals(updatedCategoria, result.getCategoria())
        );

        verify(validator, times(1)).isIdValid("2");
        verify(repository, times(1)).findById(2L);
        verify(validator, times(1)).isNameUnique(updatedFunkoDto.getNombre());
        verify(repository, times(1)).save(updatedFunko);
        verify(categoriaService, times(1)).getByNombre(updatedCategoria.getNombre());
    }

    @Test
    void updateIdNotValid() {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        updatedCategoria.setNombre("CategoriaTest");
        updatedCategoria.setActivado(true);

        FunkoDto updatedFunkoDto = new FunkoDto();
        updatedFunkoDto.setNombre("FunkoTest");
        updatedFunkoDto.setPrecio(10.00);
        updatedFunkoDto.setCategoria(updatedCategoria.getNombre());

        when(validator.isIdValid("2a")).thenReturn(false);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.update("2a", updatedFunkoDto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("El id no es valido. Debe ser de tipo Long", thrown.getReason());

        verify(validator, times(1)).isIdValid("2a");
    }

    @Test
    void updateNotFound() {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        updatedCategoria.setNombre("CategoriaTest");
        updatedCategoria.setActivado(true);

        FunkoDto updatedFunkoDto = new FunkoDto();
        updatedFunkoDto.setNombre("FunkoTest");
        updatedFunkoDto.setPrecio(10.00);
        updatedFunkoDto.setCategoria(updatedCategoria.getNombre());

        Funko updatedFunko = new Funko();
        updatedFunko.setId(2L);
        updatedFunko.setNombre("FunkoTest");
        updatedFunko.setPrecio(10.00);
        updatedFunko.setCategoria(updatedCategoria);

        when(validator.isIdValid("2")).thenReturn(true);
        when(repository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.update("2", updatedFunkoDto)
        );

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("No existe el funko con id 2", thrown.getReason());

        verify(validator, times(1)).isIdValid("2");
        verify(repository, times(1)).findById(2L);
    }

    @Test
    void updateNameNotValid() {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        updatedCategoria.setNombre("CategoriaTest");
        updatedCategoria.setActivado(true);

        FunkoDto updatedFunkoDto = new FunkoDto();
        updatedFunkoDto.setNombre("FunkoTest");
        updatedFunkoDto.setPrecio(10.00);
        updatedFunkoDto.setCategoria(updatedCategoria.getNombre());

        Funko updatedFunko = new Funko();
        updatedFunko.setId(2L);
        updatedFunko.setNombre("FunkoTest");
        updatedFunko.setPrecio(10.00);
        updatedFunko.setCategoria(updatedCategoria);

        when(validator.isIdValid("2")).thenReturn(true);
        when(repository.findById(2L)).thenReturn(Optional.of(updatedFunko));
        when(validator.isNameUnique(updatedFunkoDto.getNombre())).thenReturn(false);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.update("2", updatedFunkoDto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("El nombre del funko ya existe", thrown.getReason());

        verify(validator, times(1)).isIdValid("2");
        verify(repository, times(1)).findById(2L);
        verify(validator, times(1)).isNameUnique(updatedFunkoDto.getNombre());

    }

    @Test
    void updateCategoriaNameNotFound() {
        Categoria updatedCategoria = new Categoria();
        updatedCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        updatedCategoria.setNombre("CategoriaTest");
        updatedCategoria.setActivado(true);

        FunkoDto updatedFunkoDto = new FunkoDto();
        updatedFunkoDto.setNombre("FunkoTest");
        updatedFunkoDto.setPrecio(10.00);
        updatedFunkoDto.setCategoria(updatedCategoria.getNombre());

        Funko updatedFunko = new Funko();
        updatedFunko.setId(2L);
        updatedFunko.setNombre("FunkoTest");
        updatedFunko.setPrecio(10.00);
        updatedFunko.setCategoria(updatedCategoria);

        when(validator.isIdValid("2")).thenReturn(true);
        when(repository.findById(2L)).thenReturn(Optional.of(updatedFunko));
        when(validator.isNameUnique(updatedFunkoDto.getNombre())).thenReturn(true);
        when(categoriaService.getByNombre(updatedCategoria.getNombre())).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoria CategoriaTest no existe")
        );

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.update("2", updatedFunkoDto)
        );

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("La categoria CategoriaTest no existe", thrown.getReason());

        verify(validator, times(1)).isIdValid("2");
        verify(repository, times(1)).findById(2L);
        verify(validator, times(1)).isNameUnique(updatedFunkoDto.getNombre());
        verify(categoriaService, times(1)).getByNombre(updatedCategoria.getNombre());
    }

    @Test
    void delete() throws IOException {
        when(validator.isIdValid("1")).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(funkoTest));
        doNothing().when(webSocketHandler).sendMessage(any());


        var result = service.delete("1");

        assertAll(
                () -> assertEquals("FunkoTest", result.getNombre()),
                () -> assertEquals(10.00, result.getPrecio()),
                () -> assertEquals(categoriaTest, result.getCategoria())
        );

        verify(validator, times(1)).isIdValid("1");
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteIdNotValid() {
        when(validator.isIdValid("1a")).thenReturn(false);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.delete("1a")
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("El id no es valido. Debe ser de tipo Long", thrown.getReason());

        verify(validator, times(1)).isIdValid("1a");
    }

    @Test
    void deleteNotFound() {
        when(validator.isIdValid("1")).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class, () -> service.delete("1")
        );

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("No existe el funko con id 1", thrown.getReason());

        verify(validator, times(1)).isIdValid("1");
        verify(repository, times(1)).findById(1L);
    }
}