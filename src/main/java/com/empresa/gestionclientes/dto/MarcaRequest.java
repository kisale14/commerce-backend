package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.NotBlank;

public record MarcaRequest(
        @NotBlank(message = "El nombre de la marca es obligatorio")
        String nombre,
        String imagen,
        @NotBlank(message = "El país de origen es obligatorio")
        String paisOrigen
) {}