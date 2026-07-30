package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.CategoryRequest;
import com.empresa.gestionclientes.model.Category;
import com.empresa.gestionclientes.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Endpoints para la gestión del catálogo de categorías de productos")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar todas las categorías", description = "Retorna una lista con todas las categorías de productos registradas en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Category.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con el permiso CATEGORIES_READ")
    })
    @GetMapping
    public ResponseEntity<List<Category>> listar() {
        return ResponseEntity.ok(categoryService.obtenerCategorias());
    }

    @Operation(summary = "Obtener una categoría por ID", description = "Busca y retorna los detalles de una categoría específica a partir de su UUID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada con el ID proporcionado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso CATEGORIES_READ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> obtenerPorId(
            @Parameter(description = "UUID de la categoría a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva categoría", description = "Registra una nueva categoría de productos en el sistema mediante los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso CATEGORIES_CREATE")
    })
    @PostMapping
    public ResponseEntity<Category> crear(@RequestBody CategoryRequest request) {
        UUID nuevoId = categoryService.crearCategoria(request);
        Category nuevaCategoria = categoryService.obtenerPorId(nuevoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @Operation(summary = "Actualizar una categoría existente", description = "Modifica los datos de una categoría de productos existente identificada por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso CATEGORIES_UPDATE")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> actualizar(
            @Parameter(description = "UUID de la categoría a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody CategoryRequest request) {
        categoryService.actualizarCategoria(id, request);
        Category categoriaActualizada = categoryService.obtenerPorId(id);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @Operation(summary = "Eliminar una categoría", description = "Elimina permanentemente una categoría del sistema por medio de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso CATEGORIES_DELETE")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID de la categoría a eliminar", required = true)
            @PathVariable UUID id) {
        categoryService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}