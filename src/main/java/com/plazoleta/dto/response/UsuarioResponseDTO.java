package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// Lo que se devuelve despues de crear un usuario (nunca se devuelve la clave)
@Getter
@Setter
@Builder
public class UsuarioResponseDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String documentoDeIdentidad;
    private String celular;
    private String correo;
    private String rol;
}
