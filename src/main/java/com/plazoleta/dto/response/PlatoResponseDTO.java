package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Lo que se devuelve al crear o modificar un plato.
 * De la categoria se devuelve el nombre; del restaurante, el id.
 */
@Getter
@Setter
@Builder
public class PlatoResponseDTO {
    private Integer id;
    private String nombre;
    private Integer precio;
    private String descripcion;
    private String urlImagen;
    private Boolean estado;      // true = activo
    private String categoria;    // ej: "Hamburguesas"
    private Integer idRestaurante;
}
