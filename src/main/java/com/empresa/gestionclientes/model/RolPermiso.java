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
public class RolPermiso {
    private UUID rolId;
    private String rolNombre; // Nombre descriptivo del rol
    private UUID permisoId;
    private String permisoNombre; // Nombre descriptivo del permiso
    private LocalDateTime creadoEn;
}