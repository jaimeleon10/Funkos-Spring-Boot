package org.example.funkosProject.funko.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.funkosProject.categoria.services.CategoriaService;
import org.example.funkosProject.funko.dto.FunkoDto;
import org.example.funkosProject.funko.mappers.FunkoMapper;
import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.funko.repositories.FunkoRepository;
import org.example.funkosProject.funko.validators.FunkoValidator;
import org.example.funkosProject.notifications.config.WebSocketConfig;
import org.example.funkosProject.notifications.config.WebSocketHandler;
import org.example.funkosProject.notifications.dto.NotificationDto;
import org.example.funkosProject.notifications.mappers.NotificationMapper;
import org.example.funkosProject.notifications.models.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService{
    private final FunkoRepository repository;
    private final FunkoMapper mapper;
    private final CategoriaService categoriaService;
    private final FunkoValidator validator;
    private final WebSocketConfig webSocketConfig;
    private WebSocketHandler webSocketHandler;
    private final NotificationMapper notificacionMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public FunkoServiceImpl(FunkoRepository repository, FunkoMapper mapper, CategoriaService categoriaService, FunkoValidator validator, WebSocketConfig webSocketConfig, NotificationMapper notificacionMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoriaService = categoriaService;
        this.validator = validator;
        this.webSocketConfig = webSocketConfig;
        webSocketHandler = webSocketConfig.webSocketFunkosHandler();
        objectMapper = new ObjectMapper();
        this.notificacionMapper = notificacionMapper;
    }

    @Override
    public List<Funko> getAll() {
        log.info("Buscando todos los funkos");
        return repository.findAll();
    }

    @Cacheable
    @Override
    public Funko getById(String id) {
        log.info("Buscando funko con id: {}", id);
        if (!validator.isIdValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id no es valido. Debe ser de tipo Long");
        }
        return repository.findById(Long.parseLong(id)).orElseThrow(
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
        var categoria = categoriaService.getByNombre(funkoDto.getCategoria());
        if (repository.findByNombre(funkoDto.getNombre()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del funko ya existe");
        }
        var funko = repository.save(mapper.toFunko(funkoDto, categoria));
        onChange(Notification.Tipo.CREATE, funko);
        return funko;
    }

    @CachePut
    @Override
    public Funko update(String id, FunkoDto funkoDto) {
        log.info("Actualizando funko con id: {}", id);
        if (!validator.isIdValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id no es valido. Debe ser de tipo Long");
        }
        var res = repository.findById(Long.parseLong(id)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el funko con id " + id)
        );
        if (repository.findByNombre(funkoDto.getNombre()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del funko ya existe");
        }
        var categoria = categoriaService.getByNombre(funkoDto.getCategoria());
        res.setNombre(funkoDto.getNombre());
        res.setPrecio(funkoDto.getPrecio());
        res.setCategoria(categoria);
        var funko = repository.save(res);
        onChange(Notification.Tipo.UPDATE, funko);
        return funko;
    }

    @CacheEvict
    @Override
    public Funko delete(String id) {
        log.info("Borrando funko con id: {}", id);
        if (!validator.isIdValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id no es valido. Debe ser de tipo Long");
        }
        Funko funko = repository.findById(Long.parseLong(id)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el funko con id " + id)
        );
        repository.deleteById(Long.parseLong(id));
        onChange(Notification.Tipo.DELETE, funko);
        return funko;
    }

    void onChange(Notification.Tipo tipo, Funko data) {
        log.debug("Servicio de funkos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketHandler == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketHandler = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notification<NotificationDto> notification = new Notification<>(
                    "FUNKOS",
                    tipo,
                    notificacionMapper.toNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notification));

            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketHandler.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }
}
