package com.plazoleta.controller;

import com.plazoleta.dto.request.LoginRequestDTO;
import com.plazoleta.dto.response.LoginResponseDTO;
import com.plazoleta.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Puerta de entrada del login (HU-05).
 * Es el unico endpoint que se puede usar SIN token (regla en SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST http://localhost:8080/api/v1/auth/login
     * Body: { "correo": "...", "clave": "..." }
     * Respuesta 200: { "token": "eyJ..." }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }
}
