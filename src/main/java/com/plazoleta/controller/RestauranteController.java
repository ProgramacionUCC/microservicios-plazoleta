package com.plazoleta.controller;

import com.plazoleta.dto.request.RestauranteRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
import com.plazoleta.dto.response.RestauranteListadoResponseDTO;
import com.plazoleta.dto.response.RestauranteResponseDTO;
import com.plazoleta.service.RestauranteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Puerta de entrada para restaurantes (HU-02 y HU-09).
 * Crear: solo ADMINISTRADOR. Listar: solo CLIENTE (reglas en SecurityConfig).
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

    /**
     * HU-09: el cliente lista los restaurantes, por orden alfabetico y paginados.
     * GET http://localhost:8080/api/v1/restaurantes?pagina=0&tamano=10
     * Solo CLIENTE (regla en SecurityConfig).
     *
     * @RequestParam toma los datos que vienen despues del "?" en la URL.
     *  - pagina: que pagina ver (empieza en 0). tamano: cuantos por pagina.
     */
    @GetMapping
    public ResponseEntity<PaginaResponseDTO<RestauranteListadoResponseDTO>> listarRestaurantes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {
        return ResponseEntity.ok(  // 200
                restauranteService.listarRestaurantes(pagina, tamano));
    }
}
