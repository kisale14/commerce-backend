package com.empresa.gestionclientes.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoRequest(
        UUID modeloId,
        UUID categoryId, // ID de la categoría enviada desde el cliente[cite: 11]
        String title,
        BigDecimal price,
        Integer stock,
        String image,
        String description
) {}