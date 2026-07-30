package com.empresa.gestionclientes.service;

import com.empresa.gestionclientes.dto.*;
import com.empresa.gestionclientes.model.Usuario;
import com.empresa.gestionclientes.repository.UsuarioRepository;
import com.empresa.gestionclientes.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UUID registrar(RegistroRequest request) {
        String passwordEncriptada = passwordEncoder.encode(request.password());
        return usuarioRepository.registrarUsuario(
                request.username(),
                request.email(),
                passwordEncriptada,
                request.rolId()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        Usuario usuario = usuarioRepository.buscarPorUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String accessToken = jwtService.generarToken(usuario.getId(), usuario.getUsername(), usuario.getRol(), usuario.getPermisos());
        String refreshToken = jwtService.generarRefreshToken(usuario.getUsername());

        // Crear la Cookie HTTP-only segura para el Access Token
        Cookie jwtCookie = new Cookie("accessToken", accessToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // Cambiar a true en producción (requiere HTTPS)
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(15 * 60); // 15 minutos (coincide con el tiempo de expiración)

        response.addCookie(jwtCookie);

        return new AuthResponse(
                null, // El token ya viaja seguro en la cookie HTTP-only y no se expone al cliente
                refreshToken,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPermisos()
        );
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getPermisos(),
                usuario.getEstado()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse refrescarToken(RefreshTokenRequest request, HttpServletResponse response) {
        String refreshToken = request.refreshToken();

        if (!jwtService.esTokenValido(refreshToken)) {
            throw new RuntimeException("Refresh Token inválido o expirado");
        }

        String username = jwtService.obtenerUsernameDelToken(refreshToken);
        Usuario usuario = usuarioRepository.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevoAccessToken = jwtService.generarToken(usuario.getId(), usuario.getUsername(), usuario.getRol(), usuario.getPermisos());
        String nuevoRefreshToken = jwtService.generarRefreshToken(usuario.getUsername());

        // Actualizar la Cookie HTTP-only con el nuevo Access Token
        Cookie jwtCookie = new Cookie("accessToken", nuevoAccessToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // Cambiar a true en producción
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(15 * 60);

        response.addCookie(jwtCookie);

        return new AuthResponse(
                null,
                nuevoRefreshToken,
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPermisos()
        );
    }

    public void logout(HttpServletResponse response) {
        // Ordena la destrucción inmediata de la cookie HTTP-only en el navegador
        Cookie jwtCookie = new Cookie("accessToken", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // <-- Elimina la cookie del navegador

        response.addCookie(jwtCookie);
    }
}