package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Lo que ve el cliente al listar restaurantes (HU-09).
 *
 * La HU dice: "Los campos devueltos de cada restaurante deben ser
 * unicamente: Nombre, UrlLogo". Por eso este DTO SOLO tiene esos dos campos:
 * aunque la tabla tenga mas (NIT, direccion...), aqui no salen.
 */
@Getter
@Setter
@Builder
public class RestauranteListadoResponseDTO {
    private String nombre;
    private String urlLogo;
}
