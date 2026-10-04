package com.plazoleta.controller;

import com.plazoleta.dto.request.PedidoRequestDTO;
import com.plazoleta.dto.response.PedidoResponseDTO;
import com.plazoleta.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Puerta de entrada para pedidos.
 */
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * HU-11: el cliente hace un pedido.
     * POST http://localhost:8080/api/v1/pedidos
     * Solo CLIENTE (regla en SecurityConfig).
     * authentication.getName() = correo del cliente que hizo login.
     */
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO,
                                                         Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)  // 201
                .body(pedidoService.crearPedido(pedidoRequestDTO, authentication.getName()));
    }
}
