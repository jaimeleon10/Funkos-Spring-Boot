package org.example.funkosProject.funko.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.funkosProject.categoria.models.Categoria;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "funkos")
@EntityListeners(AuditingEntityListener.class)
public class Funko {
    private static final Long DEFAULT_ID = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id=DEFAULT_ID;

    @Column(name = "nombre", nullable = false, unique = true)
    @NotBlank(message = "El nombre no puede ser un campo vacio")
    private String nombre;

    @Column(name = "precio", nullable = false)
    @Min(value = 1, message = "El precio debe ser mayor que 0")
    @Max(value = 50, message = "El precio debe ser menor que 50")
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "categoria")
    private Categoria categoria;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();
}
