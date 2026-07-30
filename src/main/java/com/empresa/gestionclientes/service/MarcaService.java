package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.MarcaRequest;
import com.empresa.gestionclientes.model.Marca;
import com.empresa.gestionclientes.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('BRANDS_READ')")
    public List<Marca> obtenerMarcas() {
        return marcaRepository.listarMarcas();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('BRANDS_READ')")
    public Marca obtenerPorId(UUID id) {
        return marcaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('BRANDS_CREATE')")
    public UUID crearMarca(MarcaRequest request) {
        return marcaRepository.guardar(
                request.nombre(),
                request.imagen(),
                request.paisOrigen()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('BRANDS_UPDATE')")
    public void actualizarMarca(UUID id, MarcaRequest request) {
        // Aseguramos que la marca exista antes de actualizar
        obtenerPorId(id);

        marcaRepository.actualizar(
                id,
                request.nombre(),
                request.imagen(),
                request.paisOrigen()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('BRANDS_DELETE')")
    public void eliminarMarca(UUID id) {
        // Aseguramos que la marca exista antes de eliminar
        obtenerPorId(id);
        marcaRepository.eliminar(id);
    }
}