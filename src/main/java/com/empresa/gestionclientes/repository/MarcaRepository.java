package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.Marca;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MarcaRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Marca> marcaRowMapper = (rs, rowNum) -> Marca.builder()
            .id((UUID) rs.getObject("id"))
            .nombre(rs.getString("nombre"))
            .imagen(rs.getString("imagen"))
            .paisOrigen(rs.getString("pais_origen"))
            .build();

    public List<Marca> listarMarcas() {
        String sql = "SELECT id, nombre, imagen, pais_origen FROM marcas";
        return jdbcTemplate.query(sql, marcaRowMapper);
    }

    public Optional<Marca> buscarPorId(UUID id) {
        String sql = "SELECT id, nombre, imagen, pais_origen FROM marcas WHERE id = ?";
        List<Marca> marcas = jdbcTemplate.query(sql, marcaRowMapper, id);
        return marcas.stream().findFirst();
    }

    // Método para guardar (invoca el Stored Procedure)
    public UUID guardar(String nombre, String imagen, String paisOrigen) {
        String sql = "SELECT sp_registrar_marca(?, ?, ?)";
        return jdbcTemplate.queryForObject(sql, UUID.class, nombre, imagen, paisOrigen);
    }

    // Método para actualizar
    public void actualizar(UUID id, String nombre, String imagen, String paisOrigen) {
        String sql = "UPDATE marcas SET nombre = ?, imagen = ?, pais_origen = ? WHERE id = ?";
        jdbcTemplate.update(sql, nombre, imagen, paisOrigen, id);
    }

    // Método para eliminar
    public void eliminar(UUID id) {
        String sql = "DELETE FROM marcas WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}