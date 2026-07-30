package com.empresa.gestionclientes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRol {
    private UUID usuarioId;
    private String username;
    private UUID rolId;
    private String rolNombre;
    private LocalDateTime creadoEn;
}