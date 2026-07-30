package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Permission> permissionRowMapper = (rs, rowNum) -> Permission.builder()
            .id((UUID) rs.getObject("id"))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .creadoEn(rs.getTimestamp("creado_en") != null ? rs.getTimestamp("creado_en").toLocalDateTime() : null)
            .build();

    public List<Permission> listarPermisos() {
        String sql = "SELECT id, nombre, descripcion, creado_en FROM auth.permisos ORDER BY nombre ASC";
        return jdbcTemplate.query(sql, permissionRowMapper);
    }

    public Optional<Permission> buscarPorId(UUID id) {
        String sql = "SELECT id, nombre, descripcion, creado_en FROM auth.permisos WHERE id = ?";
        return jdbcTemplate.query(sql, permissionRowMapper, id).stream().findFirst();
    }

    public UUID guardar(String nombre, String descripcion) {
        String sql = "INSERT INTO auth.permisos (nombre, descripcion) VALUES (?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql, UUID.class, nombre, descripcion);
    }

    public void actualizar(UUID id, String nombre, String descripcion) {
        String sql = "UPDATE auth.permisos SET nombre = ?, descripcion = ? WHERE id = ?";
        jdbcTemplate.update(sql, nombre, descripcion, id);
    }

    public void eliminar(UUID id) {
        String sql = "DELETE FROM auth.permisos WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}