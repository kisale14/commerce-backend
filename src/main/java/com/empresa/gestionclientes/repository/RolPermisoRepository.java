package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.RolPermiso;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RolPermisoRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<RolPermiso> rolPermisoRowMapper = (rs, rowNum) -> RolPermiso.builder()
            .rolId((UUID) rs.getObject("rol_id"))
            .rolNombre(rs.getString("rol_nombre"))
            .permisoId((UUID) rs.getObject("permiso_id"))
            .permisoNombre(rs.getString("permiso_nombre"))
            .creadoEn(rs.getTimestamp("creado_en") != null ? rs.getTimestamp("creado_en").toLocalDateTime() : null)
            .build();

    public List<RolPermiso> listarAsignaciones() {
        String sql = "SELECT rp.rol_id, r.nombre AS rol_nombre, rp.permiso_id, p.nombre AS permiso_nombre, rp.creado_en " +
                "FROM auth.roles_permisos rp " +
                "JOIN auth.roles r ON rp.rol_id = r.id " +
                "JOIN auth.permisos p ON rp.permiso_id = p.id " +
                "ORDER BY r.nombre ASC, p.nombre ASC";
        return jdbcTemplate.query(sql, rolPermisoRowMapper);
    }

    public List<RolPermiso> listarPorRolId(UUID rolId) {
        String sql = "SELECT rp.rol_id, r.nombre AS rol_nombre, rp.permiso_id, p.nombre AS permiso_nombre, rp.creado_en " +
                "FROM auth.roles_permisos rp " +
                "JOIN auth.roles r ON rp.rol_id = r.id " +
                "JOIN auth.permisos p ON rp.permiso_id = p.id " +
                "WHERE rp.rol_id = ? " +
                "ORDER BY p.nombre ASC";
        return jdbcTemplate.query(sql, rolPermisoRowMapper, rolId);
    }

    public void asignarPermiso(UUID rolId, UUID permisoId) {
        String sql = "INSERT INTO auth.roles_permisos (rol_id, permiso_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, rolId, permisoId);
    }

    public void revocarPermiso(UUID rolId, UUID permisoId) {
        String sql = "DELETE FROM auth.roles_permisos WHERE rol_id = ? AND permiso_id = ?";
        jdbcTemplate.update(sql, rolId, permisoId);
    }
}