package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.UsuarioRol;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UsuarioRolRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UsuarioRol> usuarioRolRowMapper = (rs, rowNum) -> UsuarioRol.builder()
            .usuarioId((UUID) rs.getObject("usuario_id"))
            .username(rs.getString("username"))
            .rolId((UUID) rs.getObject("rol_id"))
            .rolNombre(rs.getString("rol_nombre"))
            .creadoEn(rs.getTimestamp("creado_en") != null ? rs.getTimestamp("creado_en").toLocalDateTime() : null)
            .build();

    public List<UsuarioRol> listarAsignaciones() {
        String sql = "SELECT ur.usuario_id, u.username, ur.rol_id, r.nombre AS rol_nombre, ur.creado_en " +
                "FROM usuarios_roles ur " +
                "JOIN usuarios u ON ur.usuario_id = u.id " +
                "JOIN roles r ON ur.rol_id = r.id " +
                "ORDER BY u.username ASC, r.nombre ASC";
        return jdbcTemplate.query(sql, usuarioRolRowMapper);
    }

    public List<UsuarioRol> listarPorUsuarioId(UUID usuarioId) {
        String sql = "SELECT ur.usuario_id, u.username, ur.rol_id, r.nombre AS rol_nombre, ur.creado_en " +
                "FROM usuarios_roles ur " +
                "JOIN usuarios u ON ur.usuario_id = u.id " +
                "JOIN roles r ON ur.rol_id = r.id " +
                "WHERE ur.usuario_id = ? " +
                "ORDER BY r.nombre ASC";
        return jdbcTemplate.query(sql, usuarioRolRowMapper, usuarioId);
    }

    public void asignarRolAUsuario(UUID usuarioId, UUID rolId) {
        String sql = "INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, usuarioId, rolId);
    }

    public void revocarRolDeUsuario(UUID usuarioId, UUID rolId) {
        String sql = "DELETE FROM usuarios_roles WHERE usuario_id = ? AND rol_id = ?";
        jdbcTemplate.update(sql, usuarioId, rolId);
    }
}