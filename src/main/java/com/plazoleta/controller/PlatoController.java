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

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoService platoService;

    // HU-03: crear plato
    // authentication.getName() es el correo del usuario que hizo login
    @PostMapping
    public ResponseEntity<PlatoResponseDTO> crearPlato(@Valid @RequestBody PlatoRequestDTO platoRequestDTO,
                                                       Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platoService.crearPlato(platoRequestDTO, authentication.getName()));
    }

    // HU-04: modificar precio y descripcion de un plato
    @PatchMapping("/{idPlato}")
    public ResponseEntity<PlatoResponseDTO> modificarPlato(@PathVariable Integer idPlato,
                                                           @Valid @RequestBody ModificarPlatoRequestDTO modificarPlatoRequestDTO,
                                                           Authentication authentication) {
        return ResponseEntity.ok(platoService.modificarPlato(idPlato, modificarPlatoRequestDTO, authentication.getName()));
    }
}
