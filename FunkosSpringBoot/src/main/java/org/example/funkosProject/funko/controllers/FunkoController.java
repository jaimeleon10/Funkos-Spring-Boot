package org.example.funkosProject.funko.controllers;
import jakarta.validation.Valid;
import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.services.FunkoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funkos")
public class FunkoController {
    private FunkoService service;

    @Autowired
    public FunkoController(FunkoService funkoService) {
        this.service = funkoService;
    }

    @GetMapping
    public ResponseEntity<List<Funko>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Funko> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Funko> save(@Valid @RequestBody FunkoDto funkoDto) {
        var result = service.save(funkoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Funko> update(@PathVariable Long id, @Valid @RequestBody Funko funko) {
        var result = service.update(id, funko);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Funko> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}