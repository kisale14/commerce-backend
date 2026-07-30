package com.empresa.gestionclientes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String rol;
    private Set<String> permisos;
    private String estado;
}