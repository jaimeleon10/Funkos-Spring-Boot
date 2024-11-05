package org.example.funkosProject.categoria.services;

import lombok.extern.slf4j.Slf4j;
import org.example.funkosProject.categoria.dto.CategoriaDto;
import org.example.funkosProject.categoria.mappers.CategoriaMapper;
import org.example.funkosProject.categoria.models.Categoria;
import org.example.funkosProject.categoria.models.TipoCategoria;
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
    @Cacheable(key = "#id")
    public Categoria getById(UUID id) {
        log.info("Buscando categoria con id: {}", id);
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoria con id " + id)
        );
    }

    @Override
    @Cacheable(key = "#nombre")
    public Categoria getByNombre(TipoCategoria nombre) {
        log.info("Buscando categoria llamada: {}", nombre);
        return repository.findByNombre(nombre).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoria " + nombre + " no existe")
        );
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria save(CategoriaDto categoriaDto) {
        log.info("Guardando nueva categoria llamada: {}", categoriaDto.getNombre());
        if (!validator.isNombreCategoriaValido(categoriaDto.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoria no es valido. " +
                    "Nombres disponibles: SERIE, DISNEY, SUPERHEROES, PELICULA, OTROS");
        }
        return repository.save(mapper.toCategoria(categoriaDto));
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria update(UUID id, CategoriaDto categoriaDto) {
        log.info("Actualizando categoria con id: {}", id);
        var result = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoria con id " + id)
        );
        if (!validator.isNombreCategoriaValido(categoriaDto.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoria no es valido. " +
                    "Nombres disponibles: SERIE, DISNEY, SUPERHEROES, PELICULA, OTROS");
        }
        return repository.save(mapper.toCategoriaUpdate(categoriaDto, result));
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria delete(UUID id, CategoriaDto categoriaDto) {
        log.info("Borrando categoria con id: {}", id);
        var result = repository.findByIdAndActivadoTrue(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoria con id " + id)
        );
        return repository.save(mapper.toCategoriaUpdate(categoriaDto, result));
    }
}