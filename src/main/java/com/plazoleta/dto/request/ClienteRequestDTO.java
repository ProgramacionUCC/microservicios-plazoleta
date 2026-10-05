package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan cuando un cliente crea su propia cuenta (HU-08).
 *
 * Campos obligatorios que pide la HU: nombre, apellido, documento,
 * celular, correo y clave. No lleva idRol (el sistema le pone CLIENTE)
 * ni fecha de nacimiento (la HU-08 no la pide).
 * Las reglas de formato son las mismas del propietario y el empleado (misma tabla usuario).
 */
@Getter
@Setter
public class ClienteRequestDTO {

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

    // Con este correo el cliente inicia sesion para hacer pedidos
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es valido")
    private String correo;

    // Llega en texto normal; el service la encripta con bcrypt
    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
