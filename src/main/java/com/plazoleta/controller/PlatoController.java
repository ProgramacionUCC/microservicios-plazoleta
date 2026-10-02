package com.plazoleta.controller;

import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PlatoResponseDTO;
import com.plazoleta.service.PlatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoService platoService;

    // HU-03: crear plato
    @PostMapping
    public ResponseEntity<PlatoResponseDTO> crearPlato(@Valid @RequestBody PlatoRequestDTO platoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(platoService.crearPlato(platoRequestDTO));
    }
}
