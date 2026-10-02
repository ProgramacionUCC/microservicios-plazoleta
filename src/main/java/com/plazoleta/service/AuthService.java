package com.plazoleta.service;

import com.plazoleta.dto.request.LoginRequestDTO;
import com.plazoleta.dto.response.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
