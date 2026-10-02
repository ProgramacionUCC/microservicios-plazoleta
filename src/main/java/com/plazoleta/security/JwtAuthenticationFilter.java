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

// Revisa cada peticion: si trae un token valido, deja al usuario como "logueado"
// con su correo y su rol. Si no trae token o es invalido, sigue como anonimo
// y SecurityConfig decide si puede pasar o no.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims datos = jwtService.leerToken(token);
                String correo = datos.getSubject();
                String rol = datos.get("rol", String.class);

                UsernamePasswordAuthenticationToken usuarioLogueado = new UsernamePasswordAuthenticationToken(
                        correo, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                SecurityContextHolder.getContext().setAuthentication(usuarioLogueado);
            } catch (JwtException | IllegalArgumentException e) {
                // Token vencido o alterado: se ignora y la peticion sigue como anonima
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
