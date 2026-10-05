package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Lo que se devuelve despues de crear un restaurante.
 * Del propietario solo se devuelve su id (no todos sus datos ni su clave).
 */
@Getter
@Setter
@Builder
public class RestauranteResponseDTO {
    private Integer id;
    private String nombre;
    private String nit;
    private String direccion;
    private String telefono;
    private String urlLogo;
    private Integer idPropietario;
}
