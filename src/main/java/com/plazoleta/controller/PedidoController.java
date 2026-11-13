package com.plazoleta.controller;

import com.plazoleta.dto.request.EntregarPedidoRequestDTO;
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
 * Puerta de entrada para pedidos (HU-11 a HU-16).
 * Crear pedido y cancelarlo: solo CLIENTE.
 * Listar, asignarse, marcar listo y entregar un pedido: solo EMPLEADO.
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

    /**
     * HU-13: el empleado se asigna un pedido y lo pasa a EN_PREPARACION.
     * PATCH http://localhost:8080/api/v1/pedidos/5/asignar
     * Solo EMPLEADO (regla en SecurityConfig). No lleva body: el id del pedido va en la URL
     * y el empleado es quien hizo login (authentication.getName() = su correo).
     */
    @PatchMapping("/{idPedido}/asignar")
    public ResponseEntity<PedidoResponseDTO> asignarPedido(@PathVariable Integer idPedido,
                                                           Authentication authentication) {
        return ResponseEntity.ok(  // 200
                pedidoService.asignarPedido(idPedido, authentication.getName()));
    }

    /**
     * HU-14: el empleado marca el pedido como LISTO y el cliente recibe un SMS con el PIN.
     * PATCH http://localhost:8080/api/v1/pedidos/5/listo
     * Solo EMPLEADO (regla en SecurityConfig). No lleva body.
     * La respuesta NO incluye el PIN: solo lo recibe el cliente.
     */
    @PatchMapping("/{idPedido}/listo")
    public ResponseEntity<PedidoResponseDTO> marcarPedidoListo(@PathVariable Integer idPedido,
                                                               Authentication authentication) {
        return ResponseEntity.ok(  // 200
                pedidoService.marcarPedidoListo(idPedido, authentication.getName()));
    }

    /**
     * HU-15: el empleado entrega el pedido al cliente usando el PIN que el cliente recibio.
     * PATCH http://localhost:8080/api/v1/pedidos/5/entregar
     * Body: { "pin": "4827" }
     * Solo EMPLEADO (regla en SecurityConfig).
     */
    @PatchMapping("/{idPedido}/entregar")
    public ResponseEntity<PedidoResponseDTO> entregarPedido(@PathVariable Integer idPedido,
                                                            @Valid @RequestBody EntregarPedidoRequestDTO entregarPedidoRequestDTO,
                                                            Authentication authentication) {
        return ResponseEntity.ok(  // 200
                pedidoService.entregarPedido(idPedido, entregarPedidoRequestDTO.getPin(), authentication.getName()));
    }

    /**
     * HU-16: el cliente cancela su pedido.
     * PATCH http://localhost:8080/api/v1/pedidos/5/cancelar
     * Solo CLIENTE (regla en SecurityConfig). No lleva body: el id del pedido va en la URL
     * y el cliente es quien hizo login (authentication.getName() = su correo).
     */
    @PatchMapping("/{idPedido}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Integer idPedido,
                                                            Authentication authentication) {
        return ResponseEntity.ok(  // 200
                pedidoService.cancelarPedido(idPedido, authentication.getName()));
    }
}
