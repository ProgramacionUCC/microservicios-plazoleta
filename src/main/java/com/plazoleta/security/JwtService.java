package com.plazoleta.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Crea y lee los tokens JWT (HU-05).
 *
 * ¿Que es un JWT? Un "carnet" digital que se entrega al hacer login.
 * Es un texto largo (eyJhbGciOi...) que lleva adentro:
 *   - subject: el correo del usuario
 *   - rol: su rol (ADMINISTRADOR, PROPIETARIO...)
 *   - fecha de creacion y de vencimiento
 * Va FIRMADO con una clave secreta que solo conoce el servidor: si alguien
 * cambia una sola letra del token, la firma ya no coincide y se rechaza.
 */
@Service
public class JwtService {

    // @Value lee los valores de application.properties
    @Value("${jwt.secret}")
    private String claveSecreta;   // Clave para firmar los tokens

    @Value("${jwt.expiration}")
    private long duracion;         // Cuanto dura el token en milisegundos (3600000 = 1 hora)

    // Crea el token al hacer login
    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .subject(correo)                                               // Quien es
                .claim("rol", rol)                                             // Que rol tiene
                .issuedAt(new Date())                                          // Cuando se creo
                .expiration(new Date(System.currentTimeMillis() + duracion))   // Cuando vence
                .signWith(obtenerClave())                                      // Se firma
                .compact();                                                    // Se convierte en texto
    }

    // Lee el token y devuelve sus datos (correo, rol...).
    // Si esta vencido o fue alterado lanza una excepcion (la atrapa el filtro).
    public Claims leerToken(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())  // Revisa la firma
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Convierte el texto de la clave secreta en una llave para firmar (HMAC-SHA)
    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(claveSecreta.getBytes(StandardCharsets.UTF_8));
    }
}
