package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.*;
import com.empresa.gestionclientes.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación, creación de usuarios y gestión de sesión")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Ejecuta el Stored Procedure `sp_registrar_usuario` para crear un usuario en la base de datos, asociarlo a la tabla intermedia de roles y retornar el UUID generado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(example = "{\"message\": \"Usuario registrado\", \"id\": \"3c738bcb-db4e-48ce-90e2-a83440d85093\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o el rol especificado no existe",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El nombre de usuario o correo electrónico ya se encuentran registrados",
                    content = @Content
            )
    })
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody RegistroRequest request) {
        UUID id = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuario registrado", "id", id));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario ejecutando el Stored Procedure de validación. Inyecta el Access Token en una cookie HTTP-only segura y retorna el Refresh Token junto con los datos del usuario y sus permisos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas (usuario o contraseña incorrectos)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "La cuenta del usuario se encuentra inactiva",
                    content = @Content
            )
    })
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obtener perfil del usuario",
            description = "Devuelve la información detallada del usuario actualmente autenticado (incluyendo roles y arreglo de permisos) basado en la cookie o token JWT provisto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token JWT faltante, inválido o expirado",
                    content = @Content
            )
    })
    public ResponseEntity<UsuarioResponse> obtenerPerfil() {
        return ResponseEntity.ok(authService.obtenerUsuarioAutenticado());
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar Token",
            description = "Valida un Refresh Token activo enviado en el cuerpo de la petición y emite un nuevo Access Token mediante cookie HTTP-only junto a un nuevo par de tokens."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens refrescados exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh Token inválido, expirado o revocado",
                    content = @Content
            )
    })
    public ResponseEntity<AuthResponse> refrescarToken(@RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refrescarToken(request, response));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = "Endpoint para el cierre de sesión. Limpia la cookie HTTP-only del Access Token del navegador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sesión cerrada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Sesión cerrada exitosamente\"}")
                    )
            )
    })
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        // Ejecuta la destrucción de la cookie HTTP-only en el navegador
        authService.logout(response);
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
    }
}