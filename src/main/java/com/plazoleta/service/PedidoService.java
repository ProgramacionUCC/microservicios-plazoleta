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

    // HU-13: el empleado se asigna un pedido de SU restaurante y lo pasa a EN_PREPARACION.
    PedidoResponseDTO asignarPedido(Integer idPedido, String correoEmpleado);

    // HU-14: el empleado marca el pedido como LISTO. Se genera un PIN y se avisa al cliente.
    PedidoResponseDTO marcarPedidoListo(Integer idPedido, String correoEmpleado);

    // HU-15: el empleado entrega el pedido LISTO si el PIN es el que recibio el cliente.
    PedidoResponseDTO entregarPedido(Integer idPedido, String pin, String correoEmpleado);

    // HU-16: el cliente cancela un pedido SUYO que todavia esta PENDIENTE.
    PedidoResponseDTO cancelarPedido(Integer idPedido, String correoCliente);
}
