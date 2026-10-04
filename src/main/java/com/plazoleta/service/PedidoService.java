package com.plazoleta.service;

import com.plazoleta.dto.request.PedidoRequestDTO;
import com.plazoleta.dto.response.PedidoResponseDTO;

/**
 * Que se puede hacer con pedidos.
 * La logica esta en service/impl/PedidoServiceImpl.
 */
public interface PedidoService {

    // HU-11: el cliente hace un pedido.
    // correoCliente sale del token (es el cliente que hizo login).
    PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequestDTO, String correoCliente);
}
