package com.empresa.gestionclientes.dto;

import java.util.UUID;

public record MarcaResponse(
        UUID id,
        String nombre,
        String imagen,
        String paisOrigen
) {}