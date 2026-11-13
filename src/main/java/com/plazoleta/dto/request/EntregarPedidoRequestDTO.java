package com.plazoleta.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan cuando el empleado entrega un pedido (HU-15).
 * "Para pasar a Entregado el empleado digita el PIN enviado al cliente."
 *
 * El pedido va en la URL y el empleado sale del token; aqui solo llega el PIN.
 */
@Getter
@Setter
public class EntregarPedidoRequestDTO {

    @NotBlank(message = "El PIN es obligatorio")
    private String pin;
}
