package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String nombre,
        String descripcion,
        String imagen
) {}