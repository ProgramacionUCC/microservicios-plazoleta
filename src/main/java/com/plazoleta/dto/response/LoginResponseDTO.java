package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// Si el login es correcto se devuelve el token
@Getter
@Setter
@Builder
public class LoginResponseDTO {
    private String token;
}
