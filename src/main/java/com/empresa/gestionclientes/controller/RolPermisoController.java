package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.RolPermisoRequest;
import com.empresa.gestionclientes.model.RolPermiso;
import com.empresa.gestionclientes.service.RolPermisoService;
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
@RequestMapping("/api/roles-permisos")
@RequiredArgsConstructor
@Tag(name = "Roles y Permisos (Asignaciones)", description = "Endpoints para la gestión de asignaciones y relaciones entre roles y permisos del sistema")
@SecurityRequirement(name = "bearerAuth")
public class RolPermisoController {

    private final RolPermisoService rolPermisoService;

    @Operation(summary = "Listar todas las asignaciones", description = "Retorna una lista con todas las relaciones de permisos asignados a los diferentes roles del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RolPermiso.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con los permisos necesarios")
    })
    @GetMapping
    public ResponseEntity<List<RolPermiso>> listarAsignaciones() {
        return ResponseEntity.ok(rolPermisoService.obtenerAsignaciones());
    }

    @Operation(summary = "Listar permisos por rol", description = "Retorna la lista de permisos asociados específicamente a un rol de seguridad a partir de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignaciones por rol obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RolPermiso.class)))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<RolPermiso>> listarPorRol(
            @Parameter(description = "UUID del rol para consultar sus permisos asignados", required = true)
            @PathVariable UUID rolId) {
        return ResponseEntity.ok(rolPermisoService.obtenerPorRol(rolId));
    }

    @Operation(summary = "Asignar un permiso a un rol", description = "Crea una nueva relación vinculando un permiso específico a un rol de seguridad del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permiso asignado exitosamente al rol"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos o relación ya existente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @PostMapping
    public ResponseEntity<Void> asignar(@RequestBody RolPermisoRequest request) {
        rolPermisoService.asignarPermisoARol(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Revocar un permiso de un rol", description = "Elimina la relación de un permiso asignado a un rol utilizando los parámetros de consulta de ambos UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permiso revocado exitosamente del rol (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Asignación o relación no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @DeleteMapping
    public ResponseEntity<Void> revocar(
            @Parameter(description = "UUID del rol al cual se le revoca el permiso", required = true)
            @RequestParam UUID rolId,
            @Parameter(description = "UUID del permiso que se desea revocar", required = true)
            @RequestParam UUID permisoId) {
        rolPermisoService.revocarPermisoDeRol(rolId, permisoId);
        return ResponseEntity.noContent().build();
    }
}