package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan para modificar un plato (HU-04).
 *
 * La HU dice: "Solo se puede modificar precio y descripcion".
 * Por eso este DTO SOLO tiene esos dos campos: aunque en Postman manden
 * otro (ej. nombre), Spring lo ignora porque aqui no existe.
 * El id del plato llega en la URL: PATCH /api/v1/platos/{idPlato}
 */
@Getter
@Setter
public class ModificarPlatoRequestDTO {

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un numero entero mayor a 0")
    private Integer precio;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;
}
