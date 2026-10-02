package com.plazoleta.service;

import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PlatoResponseDTO;

public interface PlatoService {
    PlatoResponseDTO crearPlato(PlatoRequestDTO platoRequestDTO);
}
