package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.RolPermisoRequest;
import com.empresa.gestionclientes.model.RolPermiso;
import com.empresa.gestionclientes.repository.RolPermisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolPermisoService {

    private final RolPermisoRepository rolPermisoRepository;

    @Transactional(readOnly = true)
    public List<RolPermiso> obtenerAsignaciones() {
        return rolPermisoRepository.listarAsignaciones();
    }

    @Transactional(readOnly = true)
    public List<RolPermiso> obtenerPorRol(UUID rolId) {
        return rolPermisoRepository.listarPorRolId(rolId);
    }

    @Transactional
    public void asignarPermisoARol(RolPermisoRequest request) {
        rolPermisoRepository.asignarPermiso(request.rolId(), request.permisoId());
    }

    @Transactional
    public void revocarPermisoDeRol(UUID rolId, UUID permisoId) {
        rolPermisoRepository.revocarPermiso(rolId, permisoId);
    }
}