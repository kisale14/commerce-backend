package com.empresa.gestionclientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegistroRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "Debe ser un correo electrónico válido")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        String password,
        @NotNull(message = "El ID del rol es obligatorio")
        UUID rolId
) {}