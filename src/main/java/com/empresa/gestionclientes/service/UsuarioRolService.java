package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.UsuarioRolRequest;
import com.empresa.gestionclientes.model.UsuarioRol;
import com.empresa.gestionclientes.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;

    @Transactional(readOnly = true)
    public List<UsuarioRol> obtenerAsignaciones() {
        return usuarioRolRepository.listarAsignaciones();
    }

    @Transactional(readOnly = true)
    public List<UsuarioRol> obtenerPorUsuario(UUID usuarioId) {
        return usuarioRolRepository.listarPorUsuarioId(usuarioId);
    }

    @Transactional
    public void asignarRol(UsuarioRolRequest request) {
        usuarioRolRepository.asignarRolAUsuario(request.usuarioId(), request.rolId());
    }

    @Transactional
    public void revocarRol(UUID usuarioId, UUID rolId) {
        usuarioRolRepository.revocarRolDeUsuario(usuarioId, rolId);
    }
}