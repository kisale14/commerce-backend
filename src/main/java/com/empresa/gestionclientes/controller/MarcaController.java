package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.MarcaRequest;
import com.empresa.gestionclientes.model.Marca;
import com.empresa.gestionclientes.service.MarcaService;
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
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
@Tag(name = "Marcas", description = "Endpoints para la gestión del catálogo de marcas de vehículos")
@SecurityRequirement(name = "bearerAuth") // Indica que requiere token JWT por seguridad
public class MarcaController {

    private final MarcaService marcaService;

    @Operation(summary = "Listar todas las marcas", description = "Retorna una lista con todas las marcas registradas en el sistema ordenadas alfabéticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de marcas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Marca.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con el permiso BRANDS_READ")
    })
    @GetMapping
    public ResponseEntity<List<Marca>> listar() {
        return ResponseEntity.ok(marcaService.obtenerMarcas());
    }

    @Operation(summary = "Obtener una marca por ID", description = "Busca y retorna los detalles de una marca específica a partir de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca encontrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Marca.class))),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso BRANDS_READ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtenerPorId(
            @Parameter(description = "UUID de la marca a buscar", required = true, example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
            @PathVariable UUID id) {
        return ResponseEntity.ok(marcaService.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva marca", description = "Registra una nueva marca en el sistema mediante los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marca creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Marca.class))),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso BRANDS_CREATE")
    })
    @PostMapping
    public ResponseEntity<Marca> crear(@RequestBody MarcaRequest request) {
        UUID nuevoId = marcaService.crearMarca(request);
        Marca nuevaMarca = marcaService.obtenerPorId(nuevoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMarca);
    }

    @Operation(summary = "Actualizar una marca existente", description = "Modifica los datos de una marca existente identificada por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marca actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Marca.class))),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso BRANDS_UPDATE")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizar(
            @Parameter(description = "UUID de la marca a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody MarcaRequest request) {
        marcaService.actualizarMarca(id, request);
        Marca marcaActualizada = marcaService.obtenerPorId(id);
        return ResponseEntity.ok(marcaActualizada);
    }

    @Operation(summary = "Eliminar una marca", description = "Elimina permanentemente una marca del sistema por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Marca eliminada exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso BRANDS_DELETE")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID de la marca a eliminar", required = true)
            @PathVariable UUID id) {
        marcaService.eliminarMarca(id);
        return ResponseEntity.noContent().build();
    }
}