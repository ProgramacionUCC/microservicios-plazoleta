package com.plazoleta.controller;

import com.plazoleta.dto.request.RestauranteRequestDTO;
import com.plazoleta.dto.response.RestauranteResponseDTO;
import com.plazoleta.service.RestauranteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Puerta de entrada para restaurantes.
 */
@RestController
@RequestMapping("/api/v1/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;

    /**
     * HU-02: crear restaurante.
     * POST http://localhost:8080/api/v1/restaurantes
     * Solo ADMINISTRADOR (regla en SecurityConfig).
     * @Valid revisa el DTO; si falla responde 400 antes de entrar.
     */
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> crearRestaurante(@Valid @RequestBody RestauranteRequestDTO restauranteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)  // 201
                .body(restauranteService.crearRestaurante(restauranteRequestDTO));
    }
}
