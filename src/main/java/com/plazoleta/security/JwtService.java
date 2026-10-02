package com.plazoleta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Crea y lee los tokens JWT (HU-05).
// El token guarda el correo del usuario y su rol.
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String claveSecreta;

    @Value("${jwt.expiration}")
    private long duracion;

    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + duracion))
                .signWith(obtenerClave())
                .compact();
    }

    // Lee el token. Si esta vencido o fue alterado lanza una excepcion.
    public Claims leerToken(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(claveSecreta.getBytes(StandardCharsets.UTF_8));
    }
}
