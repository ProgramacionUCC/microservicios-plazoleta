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

@RestController
@RequestMapping("/api/v1/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;

    // HU-02: crear restaurante
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> crearRestaurante(@Valid @RequestBody RestauranteRequestDTO restauranteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restauranteService.crearRestaurante(restauranteRequestDTO));
    }
}
