package com.plazoleta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * FILTRO = codigo que se ejecuta ANTES de que la peticion llegue al controller.
 * OncePerRequestFilter = se ejecuta una sola vez por cada peticion.
 *
 * Que hace:
 *  1. Mira si la peticion trae el header "Authorization: Bearer <token>".
 *  2. Si trae un token valido, saca el correo y el rol, y deja al usuario
 *     como "logueado" para el resto de la peticion.
 *  3. Si no trae token o es invalido, la peticion sigue como anonima
 *     y SecurityConfig decide si puede pasar (login) o no (401).
 */
@Component  // Spring crea este filtro y lo conecta en SecurityConfig
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Leer el header Authorization
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            // 2. Quitar la palabra "Bearer " (7 caracteres) para quedarnos con el token
            String token = header.substring(7);
            try {
                // 3. Leer el token (si esta vencido o alterado salta al catch)
                Claims datos = jwtService.leerToken(token);
                String correo = datos.getSubject();
                String rol = datos.get("rol", String.class);

                // 4. Dejar al usuario como logueado.
                //    Spring Security espera los roles con el prefijo "ROLE_"
                //    (ej: ROLE_ADMINISTRADOR) para que funcione hasRole("ADMINISTRADOR").
                UsernamePasswordAuthenticationToken usuarioLogueado = new UsernamePasswordAuthenticationToken(
                        correo, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                SecurityContextHolder.getContext().setAuthentication(usuarioLogueado);
                // Desde aqui, en el controller, authentication.getName() devuelve este correo
            } catch (JwtException | IllegalArgumentException e) {
                // Token vencido o alterado: no se deja logueado a nadie
                SecurityContextHolder.clearContext();
            }
        }

        // 5. Dejar que la peticion siga su camino (hacia SecurityConfig y el controller)
        filterChain.doFilter(request, response);
    }
}
