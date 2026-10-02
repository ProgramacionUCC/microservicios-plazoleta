package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

// Datos que llegan para crear un plato (HU-03)
@Getter
@Setter
public class PlatoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un numero entero mayor a 0")
    private Integer precio;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotBlank(message = "La url de la imagen es obligatoria")
    private String urlImagen;

    @NotNull(message = "La categoria es obligatoria")
    private Integer idCategoria;

    @NotNull(message = "El restaurante es obligatorio")
    private Integer idRestaurante;

    // Quien esta creando el plato. En la HU-05 se reemplaza por el usuario del login.
    @NotNull(message = "El id del propietario es obligatorio")
    private Integer idPropietario;
}
