package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// Datos que llegan para crear un propietario (HU-01).
// Las reglas de formato van aqui con anotaciones.
@Getter
@Setter
public class PropietarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El documento debe ser solo numerico")
    private String documentoDeIdentidad;

    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 13, message = "El celular debe tener maximo 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El celular solo puede tener numeros y el simbolo + al inicio")
    private String celular;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaDeNacimiento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es valido")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
