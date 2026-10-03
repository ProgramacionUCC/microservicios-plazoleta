package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan desde Postman para crear la cuenta de un empleado (HU-06).
 *
 * Campos obligatorios que pide la HU: nombre, apellido, documento, celular,
 * correo, idRol y clave. Ademas se pide idRestaurante porque la HU dice
 * "empleados de SU empresa": hay que saber a que restaurante pertenece.
 *
 * No lleva fecha de nacimiento: la HU-06 no la pide.
 * Las reglas de formato son las mismas del propietario (misma tabla usuario).
 */
@Getter
@Setter
public class EmpleadoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    // Solo digitos
    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El documento debe ser solo numerico")
    private String documentoDeIdentidad;

    // Maximo 13 caracteres, + opcional al inicio
    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 13, message = "El celular debe tener maximo 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El celular solo puede tener numeros y el simbolo + al inicio")
    private String celular;

    // El empleado entra al sistema con este correo (login HU-05)
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es valido")
    private String correo;

    // La HU lo pide como campo. El service revisa que sea el id del rol EMPLEADO.
    @NotNull(message = "El idRol es obligatorio")
    private Integer idRol;

    // Llega en texto normal; el service la encripta con bcrypt
    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    // Restaurante donde va a trabajar. Debe ser del propietario que hizo login.
    @NotNull(message = "El restaurante es obligatorio")
    private Integer idRestaurante;
}
