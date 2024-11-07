package org.example.funkosProject.storage.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.funkosProject.storage.exceptions.StorageNotFound;
import org.example.funkosProject.storage.services.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    private final String endpoint = "/funkos/files";

    @Test
    public void serveFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Contenido del archivo".getBytes());

        when(storageService.loadAsResource(any())).thenReturn(mockFile.getResource());

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/" + mockFile.getName()))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals("Contenido del archivo", response.getContentAsString())
        );
    }

    @Test
    public void serveFileNotFound() throws Exception {
        String filename = "nonexistentfile.txt";

        when(storageService.loadAsResource(anyString())).thenThrow(new StorageNotFound("File not found"));
        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/" + filename)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        verify(storageService, times(1)).loadAsResource(filename);
    }

    @Test
    public void serveFileUnknownContentType() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "archivo.desconocido", null, "Contenido".getBytes());

        when(storageService.loadAsResource(any())).thenReturn(mockFile.getResource());

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/" + mockFile.getName()))
                .andReturn().getResponse();

        assertEquals("application/octet-stream", response.getContentType());
    }
}