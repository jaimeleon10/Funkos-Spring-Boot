package org.example.funkosProject.funko.services;

import lombok.extern.slf4j.Slf4j;
import org.example.funkosProject.categoria.services.CategoriaService;
import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.mappers.FunkoMapper;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.example.funkosProject.funko.validators.FunkoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService{
    private final FunkoRepository repository;
    private final FunkoMapper mapper;
    private final CategoriaService categoriaService;
    private final FunkoValidator validator;

    @Autowired
    public FunkoServiceImpl(FunkoRepository repository, FunkoMapper mapper, CategoriaService categoriaService, FunkoValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoriaService = categoriaService;
        this.validator = validator;
    }

    @Override
    public List<Funko> getAll() {
        log.info("Buscando todos los funkos");
        return repository.findAll();
    }

    @Cacheable
    @Override
    public Funko getById(Long id) {
        log.info("Buscando funko con id: {}", id);
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el funko con id " + id)
        );
    }


    @Cacheable
    @Override
    public Funko getByNombre(String nombre) {
        log.info("Buscando funko llamado: {}", nombre);
        return repository.findByNombre(nombre).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El funko llamado " + nombre + " no existe")
        );
    }

    @CachePut
    @Override
    public Funko save(FunkoDto funkoDto) {
        log.info("Guardando nuevo funko llamado: {}", funkoDto.getNombre());
        var categoria = categoriaService.getByNombre(funkoDto.getCategoria().toUpperCase());
        if (!validator.isNameUnique(funkoDto.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del funko ya existe ");
        }
        return repository.save(mapper.toFunko(funkoDto, categoria));
    }

    @CachePut
    @Override
    public Funko update(Long id, FunkoDto funkoDto) {
        log.info("Actualizando funko con id: {}", id);
        var res = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el funko con id " + id)
        );
        if (!validator.isNameUnique(funkoDto.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del funko ya existe ");
        }
        var categoria = categoriaService.getByNombre(funkoDto.getCategoria().toUpperCase());
        res.setNombre(funkoDto.getNombre());
        res.setPrecio(funkoDto.getPrecio());
        res.setCategoria(categoria);
        return repository.save(res);
    }

    @CacheEvict
    @Override
    public Funko delete(Long id) {
        log.info("Borrando funko con id: {}", id);
        Funko funko = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el funko con id " + id)
        );
        repository.deleteById(id);
        return funko;
    }
}
