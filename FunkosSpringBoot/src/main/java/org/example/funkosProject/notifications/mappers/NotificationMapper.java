package org.example.funkosProject.notifications.mappers;

import org.example.funkosProject.funko.models.Funko;
import org.example.funkosProject.notifications.dto.NotificationDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationDto toNotificationDto(Funko funko) {
        return new NotificationDto(
                funko.getId(),
                funko.getNombre(),
                funko.getCategoria().getNombre(),
                funko.getPrecio(),
                funko.getCreatedAt().toString(),
                funko.getUpdatedAt().toString()
        );
    }
}
