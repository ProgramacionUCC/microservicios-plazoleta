package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Un plato dentro de la respuesta del pedido.
 */
@Getter
@Setter
@Builder
public class PlatoPedidoResponseDTO {
    private Integer idPlato;
    private String nombre;
    private Integer cantidad;
}
