package com.plazoleta.service;

import com.plazoleta.dto.request.RestauranteRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
import com.plazoleta.dto.response.RestauranteListadoResponseDTO;
import com.plazoleta.dto.response.RestauranteResponseDTO;

/**
 * Que se puede hacer con restaurantes.
 * La logica esta en service/impl/RestauranteServiceImpl.
 */
public interface RestauranteService {

    // HU-02: crear restaurante asociado a un propietario
    RestauranteResponseDTO crearRestaurante(RestauranteRequestDTO restauranteRequestDTO);

    // HU-09: listar restaurantes para el cliente (orden alfabetico y paginado)
    PaginaResponseDTO<RestauranteListadoResponseDTO> listarRestaurantes(int pagina, int tamano);
}
