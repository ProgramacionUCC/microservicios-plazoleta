package com.plazoleta.service;

import com.plazoleta.dto.request.PropietarioRequestDTO;
import com.plazoleta.dto.response.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO crearPropietario(PropietarioRequestDTO propietarioRequestDTO);
}
