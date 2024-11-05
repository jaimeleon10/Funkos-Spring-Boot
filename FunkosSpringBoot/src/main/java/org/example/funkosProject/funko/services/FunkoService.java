package org.example.funkosProject.funko.services;

import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.models.Funko;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FunkoService {
    List<Funko> getAll();
    Funko getById(Long id);
    Funko save(FunkoDto funkoDto);
    Funko update(Long id, FunkoDto funkoDto);
    Funko delete(Long id);
}
