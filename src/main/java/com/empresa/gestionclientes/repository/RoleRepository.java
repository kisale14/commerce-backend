package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Role> roleRowMapper = (rs, rowNum) -> Role.builder()
            .id((UUID) rs.getObject("id"))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .creadoEn(rs.getTimestamp("creado_en") != null ? rs.getTimestamp("creado_en").toLocalDateTime() : null)
            .build();

    public List<Role> listarRoles() {
        // Apuntando a la tabla roles dentro del esquema auth
        String sql = "SELECT id, nombre, descripcion, creado_en FROM auth.roles ORDER BY nombre ASC";
        return jdbcTemplate.query(sql, roleRowMapper);
    }

    public Optional<Role> buscarPorId(UUID id) {
        // Apuntando a la tabla roles dentro del esquema auth
        String sql = "SELECT id, nombre, descripcion, creado_en FROM auth.roles WHERE id = ?";
        return jdbcTemplate.query(sql, roleRowMapper, id).stream().findFirst();
    }

    public UUID guardar(String nombre, String descripcion) {
        // Apuntando a la tabla roles dentro del esquema auth
        String sql = "INSERT INTO auth.roles (nombre, descripcion) VALUES (?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql, UUID.class, nombre, descripcion);
    }

    public void actualizar(UUID id, String nombre, String descripcion) {
        // Apuntando a la tabla roles dentro del esquema auth
        String sql = "UPDATE auth.roles SET nombre = ?, descripcion = ? WHERE id = ?";
        jdbcTemplate.update(sql, nombre, descripcion, id);
    }

    public void eliminar(UUID id) {
        // Apuntando a la tabla roles dentro del esquema auth
        String sql = "DELETE FROM auth.roles WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}