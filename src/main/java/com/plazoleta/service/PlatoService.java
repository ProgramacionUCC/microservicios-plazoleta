package com.plazoleta.service;

import com.plazoleta.dto.request.ModificarPlatoRequestDTO;
import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PlatoResponseDTO;

public interface PlatoService {
    // correoUsuario: el correo del usuario que hizo login (sale del token)
    PlatoResponseDTO crearPlato(PlatoRequestDTO platoRequestDTO, String correoUsuario);
    PlatoResponseDTO modificarPlato(Integer idPlato, ModificarPlatoRequestDTO modificarPlatoRequestDTO, String correoUsuario);
}
