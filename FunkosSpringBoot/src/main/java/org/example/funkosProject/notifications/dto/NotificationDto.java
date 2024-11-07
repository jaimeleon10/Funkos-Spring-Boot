package org.example.funkosProject.notifications.dto;

public record NotificationDto(
        Long id,
        String nombre,
        String categoria,
        Double precio,
        String createdAt,
        String updatedAt
) {
}