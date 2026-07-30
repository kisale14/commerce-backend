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
public class Marca {
    private UUID id;
    private String nombre;
    private String imagen;
    private String paisOrigen;
}