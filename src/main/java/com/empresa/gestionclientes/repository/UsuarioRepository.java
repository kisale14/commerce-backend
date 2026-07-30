package com.empresa.gestionclientes.repository;

import com.empresa.gestionclientes.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Usuario> usuarioRowMapper = (rs, rowNum) -> {
        Array permisosArray = rs.getArray("permisos");
        Set<String> permisosSet = new HashSet<>();
        if (permisosArray != null) {
            String[] permisosString = (String[]) permisosArray.getArray();
            permisosSet = Set.of(permisosString);
        }

        return Usuario.builder()
                .id((UUID) rs.getObject("id"))
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .rol(rs.getString("rol"))
                .permisos(permisosSet) // <-- Asignamos los permisos mapeados correctamente
                .estado(rs.getString("estado"))
                .build();
    };

    // Registrar Usuario y asociar su rol en la tabla intermedia
    public UUID registrarUsuario(String username, String email, String passwordHash, UUID rolId) {
        // 1. Insertar el usuario en la tabla 'auth.usuarios'
        String sqlUsuario = "INSERT INTO auth.usuarios (username, email, password, rol) " +
                "VALUES (?, ?, ?, (SELECT nombre FROM auth.roles WHERE id = ?)) RETURNING id";
        UUID usuarioId = jdbcTemplate.queryForObject(sqlUsuario, UUID.class, username, email, passwordHash, rolId);

        // 2. Insertar la relación en la tabla intermedia 'auth.usuarios_roles'
        String sqlRelacion = "INSERT INTO auth.usuarios_roles (usuario_id, rol_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sqlRelacion, usuarioId, rolId);

        return usuarioId;
    }

    // Invocar Stored Procedure de Consulta (Login / Me)
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT * FROM sp_obtener_usuario_por_username(?)";
        return jdbcTemplate.query(sql, usuarioRowMapper, username)
                .stream()
                .findFirst();
    }
}