package com.plazoleta.service;

import com.plazoleta.dto.request.LoginRequestDTO;
import com.plazoleta.dto.response.LoginResponseDTO;

/**
 * Autenticacion (HU-05): iniciar sesion y recibir un token.
 */
public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
