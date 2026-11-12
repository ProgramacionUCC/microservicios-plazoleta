package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Lo que se devuelve despues de crear un pedido (HU-11).
 */
@Getter
@Setter
@Builder
public class PedidoResponseDTO {
    private Integer id;
    private String estado;           // PENDIENTE al crearse
    private Integer idRestaurante;
    private Integer idEmpleado;      // null si todavia nadie se asigno el pedido (HU-13)
    private List<PlatoPedidoResponseDTO> platos;
}
