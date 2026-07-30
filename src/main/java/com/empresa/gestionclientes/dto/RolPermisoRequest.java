package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RolPermisoRequest(
        @NotNull(message = "El ID del rol es obligatorio")
        UUID rolId,
        @NotNull(message = "El ID del permiso es obligatorio")
        UUID permisoId
) {}