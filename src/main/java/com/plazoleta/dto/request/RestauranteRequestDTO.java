package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

// Datos que llegan para crear un restaurante (HU-02)
@Getter
@Setter
public class RestauranteRequestDTO {

    // Puede tener numeros, pero no puede ser solo numeros
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = ".*[^0-9].*", message = "El nombre no puede ser solo numeros")
    private String nombre;

    @NotBlank(message = "El NIT es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El NIT debe ser solo numerico")
    private String nit;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 13, message = "El telefono debe tener maximo 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El telefono solo puede tener numeros y el simbolo + al inicio")
    private String telefono;

    @NotBlank(message = "La url del logo es obligatoria")
    private String urlLogo;

    @NotNull(message = "El id del propietario es obligatorio")
    private Integer idPropietario;
}
