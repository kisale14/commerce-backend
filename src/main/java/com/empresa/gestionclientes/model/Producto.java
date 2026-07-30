package com.empresa.gestionclientes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    private UUID id;
    private String modeloNombre;
    private String categoryNombre; // Nombre de la categoría obtenido por JOIN[cite: 10]
    private String title;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}