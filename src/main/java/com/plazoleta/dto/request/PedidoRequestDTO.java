package com.plazoleta.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Datos que llegan cuando el cliente hace un pedido (HU-11).
 * "Todo pedido debe especificar el restaurante, los platos escogidos
 *  y la cantidad de cada uno de esos platos."
 *
 * No se manda el cliente: sale del token del login.
 * No se manda el estado: todo pedido nace en PENDIENTE (lo pone el service).
 */
@Getter
@Setter
public class PedidoRequestDTO {

    @NotNull(message = "El restaurante es obligatorio")
    private Integer idRestaurante;

    // @NotEmpty: la lista no puede venir vacia (minimo un plato)
    // @Valid: revisa tambien las reglas de cada plato de la lista (idPlato y cantidad)
    @NotEmpty(message = "El pedido debe tener al menos un plato")
    @Valid
    private List<PlatoPedidoRequestDTO> platos;
}
