package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.RoleRequest;
import com.empresa.gestionclientes.model.Role;
import com.empresa.gestionclientes.service.RoleService;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints para la gestión de roles y permisos de acceso en el sistema")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Listar todos los roles", description = "Retorna una lista con todos los roles registrados en el sistema de seguridad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Role.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con el permiso ROLES_READ")
    })
    @GetMapping
    public ResponseEntity<List<Role>> listar() {
        return ResponseEntity.ok(roleService.obtenerRoles());
    }

    @Operation(summary = "Obtener un rol por ID", description = "Busca y retorna los detalles de un rol específico a partir de su UUID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso ROLES_READ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Role> obtenerPorId(
            @Parameter(description = "UUID del rol a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(roleService.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo rol", description = "Registra un nuevo rol de seguridad en el sistema mediante los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso ROLES_CREATE")
    })
    @PostMapping
    public ResponseEntity<Role> crear(@RequestBody RoleRequest request) {
        UUID nuevoId = roleService.crearRol(request);
        Role nuevoRol = roleService.obtenerPorId(nuevoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
    }

    @Operation(summary = "Actualizar un rol existente", description = "Modifica los datos y permisos asociados a un rol existente identificado por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso ROLES_UPDATE")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Role> actualizar(
            @Parameter(description = "UUID del rol a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody RoleRequest request) {
        roleService.actualizarRol(id, request);
        Role rolActualizado = roleService.obtenerPorId(id);
        return ResponseEntity.ok(rolActualizado);
    }

    @Operation(summary = "Eliminar un rol", description = "Elimina permanentemente un rol del sistema por medio de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso ROLES_DELETE")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID del rol a eliminar", required = true)
            @PathVariable UUID id) {
        roleService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
}