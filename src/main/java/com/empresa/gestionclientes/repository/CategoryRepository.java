package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> Category.builder()
            .id((UUID) rs.getObject("id"))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .imagen(rs.getString("imagen"))
            .creadoEn(rs.getTimestamp("creado_en") != null ? rs.getTimestamp("creado_en").toLocalDateTime() : null)
            .build();

    public List<Category> listarCategorias() {
        String sql = "SELECT id, nombre, descripcion, imagen, creado_en FROM categories ORDER BY nombre ASC";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public Optional<Category> buscarPorId(UUID id) {
        String sql = "SELECT id, nombre, descripcion, imagen, creado_en FROM categories WHERE id = ?";
        return jdbcTemplate.query(sql, categoryRowMapper, id).stream().findFirst();
    }

    public UUID guardar(String nombre, String descripcion, String imagen) {
        String sql = "INSERT INTO categories (nombre, descripcion, imagen) VALUES (?, ?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql, UUID.class, nombre, descripcion, imagen);
    }

    public void actualizar(UUID id, String nombre, String descripcion, String imagen) {
        String sql = "UPDATE categories SET nombre = ?, descripcion = ?, imagen = ? WHERE id = ?";
        jdbcTemplate.update(sql, nombre, descripcion, imagen, id);
    }

    public void eliminar(UUID id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}