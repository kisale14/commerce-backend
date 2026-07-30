package com.empresa.gestionclientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "Verificación de estado de la aplicación y base de datos")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @Operation(summary = "Verificar estado de la BD", description = "Retorna true si la conexión con PostgreSQL está activa, false en caso contrario.")
    public ResponseEntity<Boolean> checkHealth() {
        try {
            // Ejecuta una consulta súper liviana para probar el socket con PostgreSQL
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            boolean isDbAlive = result != null && result == 1;
            return ResponseEntity.ok(isDbAlive);
        } catch (Exception e) {
            // Si la BD no responde o falló la conexión, retorna false
            return ResponseEntity.ok(false);
        }
    }
}