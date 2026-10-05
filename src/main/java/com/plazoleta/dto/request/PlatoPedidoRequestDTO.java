package com.plazoleta.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * Un plato dentro del pedido, con su cantidad (HU-11).
 * Ej: { "idPlato": 1, "cantidad": 2 }
 */
@Getter
@Setter
public class PlatoPedidoRequestDTO {

    @NotNull(message = "El id del plato es obligatorio")
    private Integer idPlato;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}
