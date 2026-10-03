package com.plazoleta.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan para habilitar/deshabilitar un plato (HU-07).
 *
 * La HU dice: "activar/desactivar platos en el menu".
 * Por eso este DTO SOLO tiene el campo activo: aunque en Postman manden
 * otro (ej. precio), Spring lo ignora porque aqui no existe.
 * El id del plato llega en la URL: PATCH /api/v1/platos/{idPlato}/estado
 * El campo activo se guarda en Plato.estado (true = activo, false = inactivo).
 */
@Getter
@Setter
public class HabilitarPlatoRequestDTO {

    @NotNull(message = "El campo activo es obligatorio (true o false)")
    private Boolean activo;
}
