package com.plazoleta.controller;

import com.plazoleta.dto.request.PropietarioRequestDTO;
import com.plazoleta.dto.response.UsuarioResponseDTO;
import com.plazoleta.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // HU-01: crear propietario
    // @Valid hace que se revisen las reglas del DTO antes de entrar
    @PostMapping("/propietario")
    public ResponseEntity<UsuarioResponseDTO> crearPropietario(@Valid @RequestBody PropietarioRequestDTO propietarioRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crearPropietario(propietarioRequestDTO));
    }
}
