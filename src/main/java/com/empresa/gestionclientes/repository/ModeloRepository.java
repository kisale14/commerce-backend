package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.Modelo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ModeloRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Modelo> modeloRowMapper = (rs, rowNum) -> Modelo.builder()
            .id((UUID) rs.getObject("id"))
            .marcaId((UUID) rs.getObject("marca_id"))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .anioLanzamiento(rs.getInt("anio_lanzamiento"))
            .build();

    public List<Modelo> listarModelos() {
        String sql = "SELECT id, marca_id, nombre, descripcion, anio_lanzamiento FROM modelos";
        return jdbcTemplate.query(sql, modeloRowMapper);
    }

    public List<Modelo> listarPorMarcaId(UUID marcaId) {
        String sql = "SELECT id, marca_id, nombre, descripcion, anio_lanzamiento FROM modelos WHERE marca_id = ?";
        return jdbcTemplate.query(sql, modeloRowMapper, marcaId);
    }

    public Optional<Modelo> buscarPorId(UUID id) {
        String sql = "SELECT id, marca_id, nombre, descripcion, anio_lanzamiento FROM modelos WHERE id = ?";
        List<Modelo> modelos = jdbcTemplate.query(sql, modeloRowMapper, id);
        return modelos.stream().findFirst();
    }

    // Método faltante: guardar (invoca el Stored Procedure)
    public UUID guardar(UUID marcaId, String nombre, String descripcion, Integer anioLanzamiento) {
        String sql = "SELECT sp_registrar_modelo(?, ?, ?, ?)";
        return jdbcTemplate.queryForObject(sql, UUID.class, marcaId, nombre, descripcion, anioLanzamiento);
    }

    // Método faltante: actualizar
    public void actualizar(UUID id, UUID marcaId, String nombre, String descripcion, Integer anioLanzamiento) {
        String sql = "UPDATE modelos SET marca_id = ?, nombre = ?, descripcion = ?, anio_lanzamiento = ? WHERE id = ?";
        jdbcTemplate.update(sql, marcaId, nombre, descripcion, anioLanzamiento, id);
    }

    // Método faltante: eliminar
    public void eliminar(UUID id) {
        String sql = "DELETE FROM modelos WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}