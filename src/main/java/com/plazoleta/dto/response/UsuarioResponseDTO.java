package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida (response) = lo que se le devuelve a Postman
 * despues de crear un usuario.
 *
 * ¿Por que no devolver la entity Usuario directamente?
 * Porque la entity tiene la clave. Con un DTO elegimos exactamente
 * que datos se muestran: la clave NUNCA sale.
 */
@Getter
@Setter
@Builder  // Se arma asi: UsuarioResponseDTO.builder().nombre("Carlos").build()
public class UsuarioResponseDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String documentoDeIdentidad;
    private String celular;
    private String correo;
    private String rol;  // Solo el nombre del rol, ej: "PROPIETARIO"
}
