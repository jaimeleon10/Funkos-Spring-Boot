package org.example.funkosProject.categoria.controllers;

import jakarta.validation.Valid;
import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.services.CategoriaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private CategoriaServiceImpl service;

    @Autowired
    public CategoriaController(CategoriaServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Categoria> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Categoria> save(@Valid @RequestBody CategoriaDto categoriaDto) {
        var result = service.save(categoriaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Categoria> update(@PathVariable UUID id, @RequestBody CategoriaDto categoriaDto) {
        var result = service.update(id, categoriaDto);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Categoria> delete(@PathVariable UUID id, @RequestBody CategoriaDto categoriaDto) {
        var result = service.delete(id, categoriaDto);
        return ResponseEntity.ok(result);
    }
}