package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UsuarioRolRequest(
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID usuarioId,
        @NotNull(message = "El ID del rol es obligatorio")
        UUID rolId
) {}