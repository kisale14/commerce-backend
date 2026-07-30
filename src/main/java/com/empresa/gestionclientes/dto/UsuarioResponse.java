package com.empresa.gestionclientes.dto;

import java.util.Set;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String username,
        String email,
        String rol,
        Set<String> permisos,
        String estado
) {}