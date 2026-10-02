package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Respuesta del login: solo el token.
 * Ese token se manda despues en cada peticion en el header:
 *   Authorization: Bearer <token>
 */
@Getter
@Setter
@Builder
public class LoginResponseDTO {
    private String token;
}
