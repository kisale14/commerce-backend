package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.PermissionRequest;
import com.empresa.gestionclientes.model.Permission;
import com.empresa.gestionclientes.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<Permission> obtenerPermisos() {
        return permissionRepository.listarPermisos();
    }

    @Transactional(readOnly = true)
    public Permission obtenerPorId(UUID id) {
        return permissionRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + id));
    }

    @Transactional
    public UUID crearPermiso(PermissionRequest request) {
        return permissionRepository.guardar(
                request.nombre(),
                request.descripcion()
        );
    }

    @Transactional
    public void actualizarPermiso(UUID id, PermissionRequest request) {
        obtenerPorId(id);
        permissionRepository.actualizar(
                id,
                request.nombre(),
                request.descripcion()
        );
    }

    @Transactional
    public void eliminarPermiso(UUID id) {
        obtenerPorId(id);
        permissionRepository.eliminar(id);
    }
}