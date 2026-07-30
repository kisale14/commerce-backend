package com.empresa.gestionclientes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Collection;

@Service
public class JwtService {

    private final SecretKey key;
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 minutos
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 días

    public JwtService(@Value("${jwt.secret:MiClaveSecretaSuperSeguraParaGestionClientes2026!}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(UUID userId, String username, String rol, Collection<String> permisos) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId.toString())
                .claim("rol", rol)
                .claim("permisos", permisos)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // Método auxiliar para extraer los permisos dentro del filtro
    public List<String> obtenerPermisosDelToken(String token) {
        Claims claims = obtenerClaims(token);
        return claims.get("permisos", List.class);
    }

    public String generarRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String obtenerUsernameDelToken(String token) {
        return obtenerClaims(token).getSubject();
    }

    public boolean esTokenValido(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}