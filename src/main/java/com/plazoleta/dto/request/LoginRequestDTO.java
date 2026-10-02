package com.plazoleta.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan para iniciar sesion (HU-05).
 * "El inicio de sesion es a traves de correo y clave."
 */
@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es valido")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
