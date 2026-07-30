package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.CategoryRequest;
import com.empresa.gestionclientes.model.Category;
import com.empresa.gestionclientes.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('CATEGORIES_READ')")
    public List<Category> obtenerCategorias() {
        return categoryRepository.listarCategorias();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('CATEGORIES_READ')")
    public Category obtenerPorId(UUID id) {
        return categoryRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('CATEGORIES_CREATE')")
    public UUID crearCategoria(CategoryRequest request) {
        return categoryRepository.guardar(
                request.nombre(),
                request.descripcion(),
                request.imagen()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('CATEGORIES_UPDATE')")
    public void actualizarCategoria(UUID id, CategoryRequest request) {
        obtenerPorId(id);
        categoryRepository.actualizar(
                id,
                request.nombre(),
                request.descripcion(),
                request.imagen()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('CATEGORIES_DELETE')")
    public void eliminarCategoria(UUID id) {
        obtenerPorId(id);
        categoryRepository.eliminar(id);
    }
}