package com.plazoleta.service;

import com.plazoleta.dto.request.RestauranteRequestDTO;
import com.plazoleta.dto.response.RestauranteResponseDTO;

public interface RestauranteService {
    RestauranteResponseDTO crearRestaurante(RestauranteRequestDTO restauranteRequestDTO);
}
