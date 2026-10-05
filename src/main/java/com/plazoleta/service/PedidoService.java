package com.plazoleta.service;

import com.plazoleta.dto.request.PedidoRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
import com.plazoleta.dto.response.PedidoResponseDTO;

/**
 * Que se puede hacer con pedidos.
 * La logica esta en service/impl/PedidoServiceImpl.
 */
public interface PedidoService {

    // HU-11: el cliente hace un pedido.
    // correoCliente sale del token (es el cliente que hizo login).
    PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequestDTO, String correoCliente);

    // HU-12: el empleado lista los pedidos de SU restaurante filtrados por estado.
    // correoEmpleado sale del token (es el empleado que hizo login).
    PaginaResponseDTO<PedidoResponseDTO> listarPedidosPorEstado(String estado, int pagina, int tamano, String correoEmpleado);
}
