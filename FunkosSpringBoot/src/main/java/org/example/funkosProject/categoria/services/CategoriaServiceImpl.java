package org.example.funkosProject.categoria.services;

import lombok.extern.slf4j.Slf4j;
import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.mappers.CategoriaMapper;
import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.repositories.CategoriaRepository;
import org.example.funkosProject.categoria.validators.CategoriaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;
    private final CategoriaValidator validator;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository repository, CategoriaMapper mapper, CategoriaValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    public List<Categoria> getAll() {
        log.info("Buscando todas las categorías");
        return repository.findAll();
    }

    @Override
    @Cacheable
    public Categoria getById(String id) {
        log.info("Buscando categoria con id: {}", id);
        if (!validator.isIdValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La id no es válida. Debe ser un UUID");
        }
        return repository.findById(UUID.fromString(id)).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoria con id " + id)
        );
    }

    @Override
    @Cacheable
    public Categoria getByNombre(String nombreCategoria) {
        log.info("Buscando categoria llamada: {}", nombreCategoria);
        return repository.findByNombre(nombreCategoria).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoria " + nombreCategoria + " no existe")
        );
    }

    @Override
    @CachePut
    public Categoria save(CategoriaDto categoriaDto) {
        log.info("Guardando nueva categoria llamada: {}", categoriaDto.getNombre());
        if (repository.findByNombre(categoriaDto.getNombre()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoria ya existe");
        }
        return repository.save(mapper.toCategoria(categoriaDto));
    }

    @Override
    @CachePut
    public Categoria update(String id, CategoriaDto categoriaDto) {
        log.info("Actualizando categoria con id: {}", id);
        if (!validator.isIdValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La id no es válida. Debe ser un UUID");
        }
        var result = repository.findById(UUID.fromString(id)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoria con id " + id)
        );
        if (repository.findByNombre(categoriaDto.getNombre()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoria ya existe");
        }
        return repository.save(mapper.toCategoriaUpdate(categoriaDto, result));
    }

    @Override
    @CachePut
    public Categoria delete(String id, CategoriaDto categoriaDto) {
        log.info("Borrando categoria con id: {}", id);
        if (!validator.isIdValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La id no es válida. Debe ser un UUID");
        }
        var result = repository.findByIdAndActivadoTrue(UUID.fromString(id)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoria con id " + id)
        );
        return repository.save(mapper.toCategoriaUpdate(categoriaDto, result));
    }
}