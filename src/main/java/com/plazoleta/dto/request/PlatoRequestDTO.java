package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan para crear un plato (HU-03).
 *
 * No se manda quien es el propietario: eso sale del token del login (HU-05).
 * Tampoco se manda el estado: todo plato nace activo (lo pone el service).
 */
@Getter
@Setter
public class PlatoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // Integer = numero entero (si mandan 25000.5 Spring lo rechaza)
    // @Positive = mayor a 0 (0 o negativo -> error)
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un numero entero mayor a 0")
    private Integer precio;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotBlank(message = "La url de la imagen es obligatoria")
    private String urlImagen;

    // Ids de la categoria y del restaurante. Que existan lo revisa el service.
    @NotNull(message = "La categoria es obligatoria")
    private Integer idCategoria;

    @NotNull(message = "El restaurante es obligatorio")
    private Integer idRestaurante;
}
