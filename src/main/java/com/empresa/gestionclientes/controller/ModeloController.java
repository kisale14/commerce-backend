package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.ModeloRequest;
import com.empresa.gestionclientes.model.Modelo;
import com.empresa.gestionclientes.service.ModeloService;
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
@RequestMapping("/api/modelos")
@RequiredArgsConstructor
@Tag(name = "Modelos", description = "Endpoints para la gestión del catálogo de modelos de vehículos")
@SecurityRequirement(name = "bearerAuth")
public class ModeloController {

    private final ModeloService modeloService;

    @Operation(summary = "Listar o filtrar modelos", description = "Retorna una lista completa de modelos o los filtra opcionalmente según el UUID de la marca proporcionado por parámetro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de modelos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Modelo.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con el permiso MODELS_READ")
    })
    @GetMapping
    public ResponseEntity<List<Modelo>> listar(
            @Parameter(description = "UUID opcional para filtrar los modelos por marca", required = false)
            @RequestParam(required = false) UUID marcaId) {
        return ResponseEntity.ok(modeloService.obtenerModelos(marcaId));
    }

    @Operation(summary = "Obtener un modelo por ID", description = "Busca y retorna los detalles de un modelo específico a partir de su UUID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Modelo.class))),
            @ApiResponse(responseCode = "404", description = "Modelo no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso MODELS_READ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Modelo> obtenerPorId(
            @Parameter(description = "UUID del modelo a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(modeloService.obtenerPorId(id));
    }

    @Operation(summary = "Listar modelos por marca", description = "Retorna todos los modelos vinculados directamente a una marca de vehículo específica mediante su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de modelos por marca obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Modelo.class)))),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso MODELS_READ")
    })
    @GetMapping("/marca/{marcaId}")
    public ResponseEntity<List<Modelo>> listarPorMarca(
            @Parameter(description = "UUID de la marca propietaria de los modelos", required = true)
            @PathVariable UUID marcaId) {
        List<Modelo> modelos = modeloService.obtenerModelos(marcaId);
        return ResponseEntity.ok(modelos);
    }

    @Operation(summary = "Crear un nuevo modelo", description = "Registra un nuevo modelo de vehículo en el sistema asociado a una marca existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Modelo creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Modelo.class))),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso MODELS_CREATE")
    })
    @PostMapping
    public ResponseEntity<Modelo> crear(@RequestBody ModeloRequest request) {
        UUID nuevoId = modeloService.crearModelo(request);
        Modelo nuevoModelo = modeloService.obtenerPorId(nuevoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoModelo);
    }

    @Operation(summary = "Actualizar un modelo existente", description = "Modifica los datos de un modelo de vehículo existente identificado por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Modelo.class))),
            @ApiResponse(responseCode = "404", description = "Modelo no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso MODELS_UPDATE")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Modelo> actualizar(
            @Parameter(description = "UUID del modelo a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody ModeloRequest request) {
        modeloService.actualizarModelo(id, request);
        Modelo modeloActualizado = modeloService.obtenerPorId(id);
        return ResponseEntity.ok(modeloActualizado);
    }

    @Operation(summary = "Eliminar un modelo", description = "Elimina permanentemente un modelo del sistema por medio de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Modelo eliminado exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Modelo no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso MODELS_DELETE")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID del modelo a eliminar", required = true)
            @PathVariable UUID id) {
        modeloService.eliminarModelo(id);
        return ResponseEntity.noContent().build();
    }
}