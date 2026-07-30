package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.PermissionRequest;
import com.empresa.gestionclientes.model.Permission;
import com.empresa.gestionclientes.service.PermissionService;
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
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permisos", description = "Endpoints para la gestión de permisos atómicos del sistema de seguridad")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Listar todos los permisos", description = "Retorna una lista con todos los permisos atómicos registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permisos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Permission.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con el permiso PERMISSIONS_READ")
    })
    @GetMapping
    public ResponseEntity<List<Permission>> listar() {
        return ResponseEntity.ok(permissionService.obtenerPermisos());
    }

    @Operation(summary = "Obtener un permiso por ID", description = "Busca y retorna los detalles de un permiso específico a partir de su UUID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permiso encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
            @ApiResponse(responseCode = "404", description = "Permiso no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PERMISSIONS_READ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Permission> obtenerPorId(
            @Parameter(description = "UUID del permiso a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(permissionService.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo permiso", description = "Registra un nuevo permiso de seguridad en el sistema mediante los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permiso creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PERMISSIONS_CREATE")
    })
    @PostMapping
    public ResponseEntity<Permission> crear(@RequestBody PermissionRequest request) {
        UUID nuevoId = permissionService.crearPermiso(request);
        Permission nuevoPermiso = permissionService.obtenerPorId(nuevoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPermiso);
    }

    @Operation(summary = "Actualizar un permiso existente", description = "Modifica los datos de un permiso existente identificado por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permiso actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
            @ApiResponse(responseCode = "404", description = "Permiso no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PERMISSIONS_UPDATE")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Permission> actualizar(
            @Parameter(description = "UUID del permiso a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody PermissionRequest request) {
        permissionService.actualizarPermiso(id, request);
        Permission permisoActualizado = permissionService.obtenerPorId(id);
        return ResponseEntity.ok(permisoActualizado);
    }

    @Operation(summary = "Eliminar un permiso", description = "Elimina permanentemente un permiso del sistema por medio de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permiso eliminado exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Permiso no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PERMISSIONS_DELETE")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID del permiso a eliminar", required = true)
            @PathVariable UUID id) {
        permissionService.eliminarPermiso(id);
        return ResponseEntity.noContent().build();
    }
}