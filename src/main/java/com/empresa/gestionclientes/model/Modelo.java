package com.empresa.gestionclientes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modelo {
    private UUID id;
    private UUID marcaId; // Referencia a la marca a la que pertenece
    private String nombre;
    private String descripcion;
    private Integer anioLanzamiento;
}