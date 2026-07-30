package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.ModeloRequest;
import com.empresa.gestionclientes.model.Modelo;
import com.empresa.gestionclientes.repository.ModeloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeloService {

    private final ModeloRepository modeloRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('MODELS_READ')")
    public List<Modelo> obtenerModelos(UUID marcaId) {
        if (marcaId != null) {
            return modeloRepository.listarPorMarcaId(marcaId);
        }
        return modeloRepository.listarModelos();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('MODELS_READ')")
    public Modelo obtenerPorId(UUID id) {
        return modeloRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Modelo no encontrado con ID: " + id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('MODELS_CREATE')")
    public UUID crearModelo(ModeloRequest request) {
        return modeloRepository.guardar(
                request.marcaId(),
                request.nombre(),
                request.descripcion(),
                request.anioLanzamiento()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('MODELS_UPDATE')")
    public void actualizarModelo(UUID id, ModeloRequest request) {
        // Aseguramos que el modelo exista antes de actualizar
        obtenerPorId(id);

        modeloRepository.actualizar(
                id,
                request.marcaId(),
                request.nombre(),
                request.descripcion(),
                request.anioLanzamiento()
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('MODELS_DELETE')")
    public void eliminarModelo(UUID id) {
        // Aseguramos que el modelo exista antes de eliminar
        obtenerPorId(id);
        modeloRepository.eliminar(id);
    }
}