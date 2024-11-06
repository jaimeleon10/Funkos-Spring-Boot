package org.example.funkosProject.funko.controllers;

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
        categoriaTest.setNombre("CategoriaTest");
        categoriaTest.setActivado(true);
        objectMapper.registerModule(new JavaTimeModule());

        funkoTest.setId(1L);
        funkoTest.setNombre("FunkoTest");
        funkoTest.setPrecio(10.00);
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
                () -> assertEquals( HttpStatus.OK.value(), response.getStatus()),
                () -> assertFalse(res.isEmpty())
        );

        verify(service, times(1)).getAll();
    }

    @Test
    void getById() throws Exception {
        when(service.getById("1")).thenReturn(funkoTest);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(funkoTest.getId(), res.getId()),
                () -> assertEquals(funkoTest.getNombre(), res.getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), res.getPrecio()),
                () -> assertEquals(funkoTest.getCategoria(), res.getCategoria())
        );

        verify(service, times(1)).getById("1");
    }

    @Test
    void save() throws Exception {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        nuevaCategoria.setNombre("CategoriaTest");
        nuevaCategoria.setActivado(true);

        FunkoDto nuevoFunkoDto = new FunkoDto();
        nuevoFunkoDto.setNombre("FunkoTest");
        nuevoFunkoDto.setPrecio(10.00);
        nuevoFunkoDto.setCategoria("CategoriaTest");

        when(service.save(nuevoFunkoDto)).thenReturn(mapper.toFunko(nuevoFunkoDto, nuevaCategoria));

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoFunkoDto)))
                .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals( HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals( mapper.toFunko(nuevoFunkoDto, nuevaCategoria).getId(), res.getId()),
                () -> assertEquals(nuevoFunkoDto.getNombre(), res.getNombre()),
                () -> assertEquals(nuevoFunkoDto.getPrecio(), res.getPrecio()),
                () -> assertEquals(mapper.toFunko(nuevoFunkoDto, nuevaCategoria).getCategoria(), res.getCategoria())
        );

        verify(service, times(1)).save(nuevoFunkoDto);
    }

    @Test
    void update() throws Exception {
        Categoria categoriaUpdate = new Categoria();
        categoriaUpdate.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        categoriaUpdate.setNombre("CategoriaTest");
        categoriaUpdate.setActivado(true);

        FunkoDto funkoUpdateDto = new FunkoDto();
        funkoUpdateDto.setNombre("FunkoTest");
        funkoUpdateDto.setPrecio(10.00);
        funkoUpdateDto.setCategoria(categoriaUpdate.getNombre());

        Funko funkoUpdate = new Funko();
        funkoUpdate.setNombre(funkoUpdateDto.getNombre());
        funkoUpdate.setPrecio(funkoUpdateDto.getPrecio());
        funkoUpdate.setCategoria(categoriaUpdate);

        when(service.update("2", funkoUpdateDto)).thenReturn(mapper.toFunkoUpdate(funkoUpdateDto, funkoUpdate, categoriaUpdate));

        MockHttpServletResponse response = mvc.perform(
                        put(myEndpoint + "/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(funkoUpdateDto)))
                .andReturn().getResponse();

        Funko res = objectMapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(funkoUpdateDto.getNombre(), res.getNombre()),
                () -> assertEquals(funkoUpdateDto.getPrecio(), res.getPrecio()),
                () -> assertEquals(funkoUpdateDto.getCategoria(), res.getCategoria().getNombre())
        );

        verify(service, times(1)).update("2", funkoUpdateDto);
    }

    @Test
    void delete() throws Exception {
        Categoria categoriaDelete = new Categoria();
        categoriaDelete.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        categoriaDelete.setNombre("CategoriaTest");
        categoriaDelete.setActivado(true);

        Funko deletedFunko = new Funko();
        deletedFunko.setNombre("FunkoDeleteTest");
        deletedFunko.setPrecio(10.00);
        deletedFunko.setCategoria(categoriaDelete);

        when(service.delete("1")).thenReturn(deletedFunko);

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.delete(myEndpoint + "/1") //Porque tengo que añadir MockMvcRequestBuilders
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals("", response.getContentAsString());

        verify(service, times(1)).delete("1");
    }
}