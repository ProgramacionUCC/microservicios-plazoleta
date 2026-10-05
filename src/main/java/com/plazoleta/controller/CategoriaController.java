package com.plazoleta.controller;

import com.plazoleta.dto.request.CategoriaRequestDTO;
import com.plazoleta.dto.response.CategoriaResponseDTO;
import com.plazoleta.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Puerta de entrada para categorias (se necesitan para crear platos, HU-03).
 * Cualquier usuario logueado puede usarlas (regla en SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // POST http://localhost:8080/api/v1/categorias -> crea una categoria (201)
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.crearCategoria(categoriaRequestDTO));
    }

    // GET http://localhost:8080/api/v1/categorias -> lista todas (200)
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }
}
