package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(
        @NotBlank(message = "El nombre del rol es obligatorio")
        String nombre,
        String descripcion
) {}