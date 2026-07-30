package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.UsuarioRolRequest;
import com.empresa.gestionclientes.model.UsuarioRol;
import com.empresa.gestionclientes.service.UsuarioRolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios-roles")
@RequiredArgsConstructor
@Tag(name = "Usuarios y Roles (Asignaciones)", description = "Endpoints para la gestión de asignaciones y relaciones entre usuarios y roles del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;

    @Operation(summary = "Listar todas las asignaciones de roles a usuarios", description = "Retorna una lista con todas las relaciones existentes entre los usuarios y los roles asignados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioRol.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con los permisos necesarios")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioRol>> listarAsignaciones() {
        return ResponseEntity.ok(usuarioRolService.obtenerAsignaciones());
    }

    @Operation(summary = "Listar roles por usuario", description = "Retorna la lista de roles de seguridad asociados específicamente a un usuario a partir de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignaciones por usuario obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioRol.class)))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<UsuarioRol>> listarPorUsuario(
            @Parameter(description = "UUID del usuario para consultar sus roles asignados", required = true)
            @PathVariable UUID usuarioId) {
        return ResponseEntity.ok(usuarioRolService.obtenerPorUsuario(usuarioId));
    }

    @Operation(summary = "Asignar un rol a un usuario", description = "Crea una nueva relación vinculando un rol específico a un usuario del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol asignado exitosamente al usuario"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos o relación ya existente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @PostMapping
    public ResponseEntity<Void> asignar(@RequestBody UsuarioRolRequest request) {
        usuarioRolService.asignarRol(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Revocar un rol de un usuario", description = "Elimina la relación de un rol asignado a un usuario utilizando los parámetros de consulta de ambos UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol revocado exitosamente del usuario (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Asignación o relación no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @DeleteMapping
    public ResponseEntity<Void> revocar(
            @Parameter(description = "UUID del usuario al cual se le revoca el rol", required = true)
            @RequestParam UUID usuarioId,
            @Parameter(description = "UUID del rol que se desea revocar", required = true)
            @RequestParam UUID rolId) {
        usuarioRolService.revocarRol(usuarioId, rolId);
        return ResponseEntity.noContent().build();
    }
}