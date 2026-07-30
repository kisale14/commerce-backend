package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductoRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Producto> productoRowMapper = (rs, rowNum) -> Producto.builder()
            .id((UUID) rs.getObject("id"))
            .modeloNombre(rs.getString("modelo_nombre"))
            .categoryNombre(rs.getString("category_nombre")) // Mapeo del nombre de la categoría[cite: 12]
            .title(rs.getString("title"))
            .price(rs.getBigDecimal("price"))
            .stock(rs.getInt("stock"))
            .image(rs.getString("image"))
            .description(rs.getString("description"))
            .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
            .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
            .build();

    public List<Producto> filtrarProductos(String search, UUID categoryId) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, m.nombre AS modelo_nombre, c.nombre AS category_nombre " +
                        "FROM products p " +
                        "LEFT JOIN modelos m ON p.modelo_id = m.id " +
                        "LEFT JOIN categories c ON p.category_id = c.id " +
                        "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND LOWER(p.title) LIKE LOWER(?)");
            params.add("%" + search + "%");
        }

        if (categoryId != null) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }

        return jdbcTemplate.query(sql.toString(), productoRowMapper, params.toArray());
    }

    public Optional<Producto> buscarPorId(UUID id) {
        String sql = "SELECT p.*, m.nombre AS modelo_nombre, c.nombre AS category_nombre " +
                "FROM products p " +
                "LEFT JOIN modelos m ON p.modelo_id = m.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE p.id = ?";
        return jdbcTemplate.query(sql, productoRowMapper, id)
                .stream()
                .findFirst();
    }

    public UUID guardar(UUID modeloId, UUID categoryId, String title, BigDecimal price, Integer stock, String image, String description) {
        String sql = "INSERT INTO products (modelo_id, category_id, title, price, stock, image, description) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql, UUID.class, modeloId, categoryId, title, price, stock, image, description);
    }

    public void actualizar(UUID id, UUID modeloId, UUID categoryId, String title, BigDecimal price, Integer stock, String image, String description) {
        String sql = "UPDATE products SET modelo_id = ?, category_id = ?, title = ?, price = ?, stock = ?, image = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, modeloId, categoryId, title, price, stock, image, description, id);
    }

    public void eliminar(UUID id) {
        String sql = "DELETE FROM products WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}