package com.empresa.gestionclientes.controller;

import com.empresa.gestionclientes.dto.ProductoRequest;
import com.empresa.gestionclientes.model.Producto;
import com.empresa.gestionclientes.service.ProductoService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para la gestión del catálogo de productos y repuestos")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Listar o filtrar productos", description = "Retorna una lista completa de productos o permite filtrarlos opcionalmente por término de búsqueda (título/descripción) y por UUID de categoría.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - El usuario no cuenta con el permiso PRODUCTS_READ")
    })
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @Parameter(description = "Término opcional para buscar productos por texto", required = false)
            @RequestParam(required = false) String search,
            @Parameter(description = "UUID opcional de la categoría para filtrar los productos", required = false)
            @RequestParam(required = false) UUID categoryId
    ) {
        return ResponseEntity.ok(productoService.obtenerProductos(search, categoryId));
    }

    @Operation(summary = "Obtener un producto por ID", description = "Busca y retorna los detalles de un producto específico a partir de su UUID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PRODUCTS_READ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(
            @Parameter(description = "UUID del producto a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear un nuevo producto", description = "Registra un nuevo producto o repuesto en el sistema vinculado a categorías y modelos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PRODUCTS_CREATE")
    })
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody ProductoRequest request) {
        UUID nuevoId = productoService.crearProducto(request);
        Producto nuevoProducto = productoService.obtenerPorId(nuevoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @Operation(summary = "Actualizar un producto existente", description = "Modifica los datos de un producto existente identificado por su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PRODUCTS_UPDATE")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @Parameter(description = "UUID del producto a actualizar", required = true)
            @PathVariable UUID id,
            @RequestBody ProductoRequest request) {
        productoService.actualizarProducto(id, request);
        Producto productoActualizado = productoService.obtenerPorId(id);
        return ResponseEntity.ok(productoActualizado);
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina permanentemente un producto del sistema por medio de su UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Falta permiso PRODUCTS_DELETE")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID del producto a eliminar", required = true)
            @PathVariable UUID id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}