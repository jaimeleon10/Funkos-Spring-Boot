package org.example.funkosProject.services.funkos.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.mappers.FunkoMapper;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.services.FunkoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FunkoControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    FunkoServiceImpl service;

    @Autowired
    MockMvc mvc;

    FunkoMapper mapper = new FunkoMapper();
    Funko funkoTest = new Funko();
    Categoria categoriaTest = new Categoria();
    String myEndpoint = "/funkos";

    @Autowired
    private FunkoControllerTest(FunkoServiceImpl service) {
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        categoriaTest.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        categoriaTest.setNombre("PELICULA");
        categoriaTest.setActivado(true);
        objectMapper.registerModule(new JavaTimeModule());

        funkoTest.setId(1L);
        funkoTest.setNombre("Darth Vader");
        funkoTest.setPrecio(10.99);
        funkoTest.setCategoria(categoriaTest);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll()).thenReturn(List.of(funkoTest));

        MockHttpServletResponse response = mvc.perform(
                get(myEndpoint)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Funko> res = objectMapper.readValue(response.getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertFalse(res.isEmpty()),
                () -> assertTrue(res.stream().anyMatch(r -> r.getId().equals(funkoTest.getId())))
        );

        verify(service, times(1)).getAll();
    }

    @Test
    void getById() throws Exception {
        when(service.getById(1L)).thenReturn(funkoTest);

        MockHttpServletResponse response = mvc.perform(
                get(myEndpoint + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getId(), funkoTest.getId()),
                () -> assertEquals(res.getNombre(), funkoTest.getNombre()),
                () -> assertEquals(res.getPrecio(), funkoTest.getPrecio()),
                () -> assertEquals(res.getCategoria(), funkoTest.getCategoria())
        );

        verify(service, times(1)).getById(1L);
    }

    @Test
    void save() throws Exception {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778777"));
        nuevaCategoria.setNombre("DISNEY");
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunko = new FunkoDto();
        nuevoFunko.setNombre("Mickey Mouse");
        nuevoFunko.setPrecio(7.95);
        nuevoFunko.setCategoria("DISNEY");

        when(service.save(nuevoFunko)).thenReturn(mapper.toFunko(nuevoFunko, nuevaCategoria));

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoFunko)))
                .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.CREATED.value()),
                () -> assertEquals(res.getId(), mapper.toFunko(nuevoFunko, nuevaCategoria).getId()),
                () -> assertEquals(res.getNombre(), nuevoFunko.getNombre()),
                () -> assertEquals(res.getPrecio(), nuevoFunko.getPrecio()),
                () -> assertEquals(res.getCategoria(), mapper.toFunko(nuevoFunko, nuevaCategoria).getCategoria())
        );

        verify(service, times(1)).save(nuevoFunko);
    }

    @Test
    void update() throws Exception {
        Categoria updateCategoria = new Categoria();
        updateCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778788"));
        updateCategoria.setNombre("SUPERHEROES");
        updateCategoria.setActivado(true);

        FunkoDto updateFunko = new FunkoDto();
        updateFunko.setNombre("Goku");
        updateFunko.setPrecio(15.99);
        updateFunko.setCategoria("DISNEY");

        when(service.update(2L, updateFunko)).thenReturn(mapper.toFunko(updateFunko, updateCategoria));

        MockHttpServletResponse response = mvc.perform(
                put(myEndpoint + "/2")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(updateFunko)))
               .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertEquals(res.getNombre(), updateFunko.getNombre()),
                () -> assertEquals(res.getPrecio(), updateFunko.getPrecio()),
                () -> assertEquals(res.getCategoria(), updateCategoria)
        );

        verify(service, times(1)).update(2L, updateFunko);
    }

    @Test
    void delete() throws Exception {
        when(service.delete(1L)).thenReturn(funkoTest);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.delete(myEndpoint + "/1")
                       .accept(MediaType.APPLICATION_JSON))
               .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value());

        verify(service, times(1)).delete(1L);
    }
}