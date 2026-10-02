package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de entrada (request) = los datos que llegan desde Postman
 * para crear un propietario (HU-01).
 *
 * Aqui van las reglas de FORMATO con anotaciones. En Java puro esto eran
 * muchos "if". Spring revisa estas anotaciones automaticamente cuando el
 * controller tiene @Valid. Si alguna falla, responde 400 con el "message".
 *
 * Las reglas que necesitan la base de datos o la fecha actual
 * (correo repetido, mayor de edad) van en el service, no aqui.
 */
@Getter
@Setter
public class PropietarioRequestDTO {

    // @NotBlank: no puede venir null, vacio "" ni solo espacios "   "
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    // @Pattern revisa con una expresion regular:
    // ^[0-9]+$  -> de inicio (^) a fin ($) solo digitos (uno o mas)
    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El documento debe ser solo numerico")
    private String documentoDeIdentidad;

    // @Size(max = 13): maximo 13 caracteres (ej: +573005698325)
    // ^\+?[0-9]+$  -> un + opcional al inicio y despues solo digitos
    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 13, message = "El celular debe tener maximo 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El celular solo puede tener numeros y el simbolo + al inicio")
    private String celular;

    // @NotNull (no @NotBlank) porque no es texto, es una fecha.
    // En Postman se manda asi: "1990-05-10"
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaDeNacimiento;

    // @Email: revisa que tenga forma de correo (algo@algo)
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es valido")
    private String correo;

    // Llega en texto normal; el service la encripta antes de guardarla
    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
