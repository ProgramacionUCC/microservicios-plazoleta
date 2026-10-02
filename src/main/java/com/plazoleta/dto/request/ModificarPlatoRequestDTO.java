package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

// HU-04: solo se puede modificar precio y descripcion
@Getter
@Setter
public class ModificarPlatoRequestDTO {

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un numero entero mayor a 0")
    private Integer precio;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    // Quien esta modificando el plato. En la HU-05 se reemplaza por el usuario del login.
    @NotNull(message = "El id del propietario es obligatorio")
    private Integer idPropietario;
}
