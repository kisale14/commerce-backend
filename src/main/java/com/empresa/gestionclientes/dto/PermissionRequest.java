package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.NotBlank;

public record PermissionRequest(
        @NotBlank(message = "El nombre del permiso es obligatorio")
        String nombre,
        String descripcion
) {}