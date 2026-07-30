package com.empresa.gestionclientes.dto;

import java.util.UUID;

public record ModeloResponse(
        UUID id,
        UUID marcaId,
        String nombre,
        String descripcion,
        Integer anioLanzamiento
) {}