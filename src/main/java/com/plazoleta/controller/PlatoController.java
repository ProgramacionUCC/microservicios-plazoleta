package com.plazoleta.controller;

import com.plazoleta.dto.request.ModificarPlatoRequestDTO;
import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PlatoResponseDTO;
import com.plazoleta.service.PlatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Puerta de entrada para platos (HU-03 y HU-04).
 * Solo PROPIETARIO puede entrar (regla en SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoService platoService;

    /**
     * HU-03: crear plato.
     * POST http://localhost:8080/api/v1/platos
     *
     * Authentication = el usuario que hizo login. Spring lo llena solo
     * gracias al JwtAuthenticationFilter. authentication.getName() es su correo.
     */
    @PostMapping
    public ResponseEntity<PlatoResponseDTO> crearPlato(@Valid @RequestBody PlatoRequestDTO platoRequestDTO,
                                                       Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)  // 201
                .body(platoService.crearPlato(platoRequestDTO, authentication.getName()));
    }

    /**
     * HU-04: modificar precio y descripcion.
     * PATCH http://localhost:8080/api/v1/platos/1
     *
     * PATCH = modificar solo una parte (a diferencia de PUT, que reemplaza todo).
     * @PathVariable toma el {idPlato} de la URL (en el ejemplo, el 1).
     */
    @PatchMapping("/{idPlato}")
    public ResponseEntity<PlatoResponseDTO> modificarPlato(@PathVariable Integer idPlato,
                                                           @Valid @RequestBody ModificarPlatoRequestDTO modificarPlatoRequestDTO,
                                                           Authentication authentication) {
        return ResponseEntity.ok(  // 200
                platoService.modificarPlato(idPlato, modificarPlatoRequestDTO, authentication.getName()));
    }
}
