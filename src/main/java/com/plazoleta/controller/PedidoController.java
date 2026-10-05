package com.plazoleta.controller;

import com.plazoleta.dto.request.PedidoRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
import com.plazoleta.dto.response.PedidoResponseDTO;
import com.plazoleta.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Puerta de entrada para pedidos (HU-11 y HU-12).
 * Crear pedido: solo CLIENTE. Listar por estado: solo EMPLEADO.
 * (reglas en SecurityConfig)
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

    /**
     * HU-12: el empleado lista los pedidos de SU restaurante filtrados por estado.
     * GET http://localhost:8080/api/v1/pedidos?estado=PENDIENTE&pagina=0&tamano=10
     * Solo EMPLEADO (regla en SecurityConfig).
     * authentication.getName() = correo del empleado que hizo login.
     *
     * @RequestParam toma los datos que vienen despues del "?" en la URL.
     */
    @GetMapping
    public ResponseEntity<PaginaResponseDTO<PedidoResponseDTO>> listarPedidosPorEstado(
            @RequestParam String estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            Authentication authentication) {
        return ResponseEntity.ok(  // 200
                pedidoService.listarPedidosPorEstado(estado, pagina, tamano, authentication.getName()));
    }
}
