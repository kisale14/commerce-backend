package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.ProductoRequest;
import com.empresa.gestionclientes.model.Producto;
import com.empresa.gestionclientes.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PRODUCTS_READ')")
    public List<Producto> obtenerProductos(String search, UUID categoryId) {
        return productoRepository.filtrarProductos(search, categoryId);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('PRODUCTS_READ')")
    public Producto obtenerPorId(UUID id) {
        return productoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('PRODUCTS_CREATE')")
    public UUID crearProducto(ProductoRequest request) {
        return productoRepository.guardar(
                request.modeloId(),
                request.categoryId(), // Pasando el ID de categoría
                request.title(),
                request.price(),
                request.stock(),
                request.image(),
                request.description()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('PRODUCTS_UPDATE')")
    public void actualizarProducto(UUID id, ProductoRequest request) {
        obtenerPorId(id);

        productoRepository.actualizar(
                id,
                request.modeloId(),
                request.categoryId(), // Pasando el ID de categoría
                request.title(),
                request.price(),
                request.stock(),
                request.image(),
                request.description()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('PRODUCTS_DELETE')")
    public void eliminarProducto(UUID id) {
        obtenerPorId(id);
        productoRepository.eliminar(id);
    }
}