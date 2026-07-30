package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.RoleRequest;
import com.empresa.gestionclientes.model.Role;
import com.empresa.gestionclientes.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> obtenerRoles() {
        return roleRepository.listarRoles();
    }

    @Transactional(readOnly = true)
    public Role obtenerPorId(UUID id) {
        return roleRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
    }

    @Transactional
    public UUID crearRol(RoleRequest request) {
        return roleRepository.guardar(
                request.nombre(),
                request.descripcion()
        );
    }

    @Transactional
    public void actualizarRol(UUID id, RoleRequest request) {
        obtenerPorId(id);
        roleRepository.actualizar(
                id,
                request.nombre(),
                request.descripcion()
        );
    }

    @Transactional
    public void eliminarRol(UUID id) {
        obtenerPorId(id);
        roleRepository.eliminar(id);
    }
}