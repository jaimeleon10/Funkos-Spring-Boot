package org.example.funkosProject.categoria.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.mappers.CategoriaMapper;
import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.services.CategoriaServiceImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    CategoriaServiceImpl service;

    @Autowired
    MockMvc mvc;

    CategoriaMapper mapper = new CategoriaMapper();
    Categoria categoriaTest = new Categoria();
    String myEndpoint = "/categorias";

    @Autowired
    private CategoriaControllerTest(CategoriaServiceImpl service) {
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        categoriaTest.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        categoriaTest.setNombre("CategoriaTest");
        categoriaTest.setCreatedAt(LocalDateTime.now());
        categoriaTest.setUpdatedAt(LocalDateTime.now());
        categoriaTest.setActivado(true);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll()).thenReturn(List.of(categoriaTest));

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Categoria> res = objectMapper.readValue(response.getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Categoria.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertFalse(res.isEmpty())
        );

        verify(service, times(1)).getAll();
    }

    @Test
    void getById() throws Exception {
        when(service.getById("4182d617-ec89-4fbc-be95-85e461778766")).thenReturn(categoriaTest);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint + "/4182d617-ec89-4fbc-be95-85e461778766")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(categoriaTest.getId(), res.getId()),
                () -> assertEquals(categoriaTest.getNombre(), res.getNombre()),
                () -> assertEquals(categoriaTest.getActivado(), res.getActivado())
        );

        verify(service, times(1)).getById("4182d617-ec89-4fbc-be95-85e461778766");
    }

    @Test
    void save() throws Exception {
        CategoriaDto nuevoCategoria = new CategoriaDto();
        nuevoCategoria.setNombre("CategoriaTest");
        nuevoCategoria.setActivado(true);

        when(service.save(nuevoCategoria)).thenReturn(mapper.toCategoria(nuevoCategoria));

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoCategoria)))
                .andReturn().getResponse();

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(mapper.toCategoria(nuevoCategoria).getId(), res.getId()),
                () -> assertEquals(mapper.toCategoria(nuevoCategoria).getNombre(), res.getNombre()),
                () -> assertEquals(nuevoCategoria.getActivado(), res.getActivado())
        );

        verify(service, times(1)).save(nuevoCategoria);
    }

    @Test
    void update() throws Exception {
        CategoriaDto updatedCategoria = new CategoriaDto();
        updatedCategoria.setNombre("CategoriaUpdateTest");
        updatedCategoria.setActivado(true);

        Categoria expectedCategoria = new Categoria();
        expectedCategoria.setId(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778766"));
        expectedCategoria.setNombre("CategoriaUpdateTest");
        expectedCategoria.setActivado(true);

        when(service.update("4182d617-ec89-4fbc-be95-85e461778766", updatedCategoria)).thenReturn(expectedCategoria);

        MockHttpServletResponse response = mvc.perform(
                        put(myEndpoint + "/4182d617-ec89-4fbc-be95-85e461778766")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedCategoria)))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);
        assertAll(
                () -> assertEquals(expectedCategoria.getId(), res.getId()),
                () -> assertEquals(expectedCategoria.getNombre(), res.getNombre()),
                () -> assertEquals(expectedCategoria.getActivado(), res.getActivado())
        );

        verify(service, times(1)).update("4182d617-ec89-4fbc-be95-85e461778766", updatedCategoria);
    }


    @Test
    void delete() throws Exception {
        CategoriaDto deletedCategoria = new CategoriaDto();
        deletedCategoria.setNombre("CategoriaDeleteTest");
        deletedCategoria.setActivado(true);

        when(service.delete("4182d617-ec89-4fbc-be95-85e461778766", deletedCategoria)).thenReturn(mapper.toCategoria(deletedCategoria));

        MockHttpServletResponse response = mvc.perform(
                        patch(myEndpoint + "/4182d617-ec89-4fbc-be95-85e461778766")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(deletedCategoria)))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Categoria res = objectMapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(mapper.toCategoria(deletedCategoria).getId(), res.getId()),
                () -> assertEquals(mapper.toCategoria(deletedCategoria).getNombre(), res.getNombre()),
                () -> assertEquals(deletedCategoria.getActivado(), res.getActivado())
        );

        verify(service, times(1)).delete("4182d617-ec89-4fbc-be95-85e461778766", deletedCategoria);
    }

    @Test
    void nombreIsBlank() throws Exception {
        CategoriaDto nuevoCategoria = new CategoriaDto();
        nuevoCategoria.setNombre("");
        nuevoCategoria.setActivado(true);

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevoCategoria)))
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        String responseContent = response.getContentAsString();

        assertTrue(responseContent.contains("El nombre no puede ser un campo vacio"));
    }

    @Test
    void testValidationExceptionHandler() throws Exception {
        // Enviar una solicitud con un campo inválido (sin nombre)
        mvc.perform(post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"\" }")) // En este caso, el campo 'nombre' está vacío, lo que debería causar una excepción de validación
                .andExpect(status().isBadRequest()) // El estado debe ser 400 Bad Request
                .andExpect(jsonPath("$.nombre").value("El nombre no puede ser un campo vacio"))
                .andReturn();
    }
}