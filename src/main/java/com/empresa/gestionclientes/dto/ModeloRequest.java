package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ModeloRequest(
        @NotNull(message = "El ID de la marca es obligatorio")
        UUID marcaId,
        @NotBlank(message = "El nombre del modelo es obligatorio")
        String nombre,
        String descripcion,
        @NotNull(message = "El año de lanzamiento es obligatorio")
        Integer anioLanzamiento
) {}