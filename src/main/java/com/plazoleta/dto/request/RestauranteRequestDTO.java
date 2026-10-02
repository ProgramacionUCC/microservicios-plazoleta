package com.plazoleta.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que llegan desde Postman para crear un restaurante (HU-02).
 * Todas las reglas de formato de la HU estan aqui con anotaciones.
 */
@Getter
@Setter
public class RestauranteRequestDTO {

    // La HU dice: puede tener numeros, pero NO puede ser solo numeros.
    // .*[^0-9].*  -> en cualquier parte debe haber al menos un caracter que NO sea digito
    //   "La Plazoleta 2" -> valido     "123" -> invalido
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = ".*[^0-9].*", message = "El nombre no puede ser solo numeros")
    private String nombre;

    // Solo digitos
    @NotBlank(message = "El NIT es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El NIT debe ser solo numerico")
    private String nit;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    // Igual que el celular del propietario: maximo 13 y + opcional al inicio
    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 13, message = "El telefono debe tener maximo 13 caracteres")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "El telefono solo puede tener numeros y el simbolo + al inicio")
    private String telefono;

    @NotBlank(message = "La url del logo es obligatoria")
    private String urlLogo;

    // Es un numero (id del usuario), por eso @NotNull y no @NotBlank.
    // Que ese usuario exista y sea PROPIETARIO lo revisa el service.
    @NotNull(message = "El id del propietario es obligatorio")
    private Integer idPropietario;
}
