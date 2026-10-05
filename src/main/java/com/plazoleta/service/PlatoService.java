package com.plazoleta.service;

import com.plazoleta.dto.request.HabilitarPlatoRequestDTO;
import com.plazoleta.dto.request.ModificarPlatoRequestDTO;
import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
import com.plazoleta.dto.response.PlatoResponseDTO;

/**
 * Que se puede hacer con platos.
 *
 * correoUsuario = el correo del usuario que hizo login. Sale del token (HU-05)
 * y sirve para revisar que sea el dueño del restaurante.
 */
public interface PlatoService {

    // HU-03: crear plato
    PlatoResponseDTO crearPlato(PlatoRequestDTO platoRequestDTO, String correoUsuario);

    // HU-04: modificar precio y descripcion
    PlatoResponseDTO modificarPlato(Integer idPlato, ModificarPlatoRequestDTO modificarPlatoRequestDTO, String correoUsuario);

    // HU-07: habilitar/deshabilitar plato
    PlatoResponseDTO cambiarEstadoPlato(Integer idPlato, HabilitarPlatoRequestDTO habilitarPlatoRequestDTO, String correoUsuario);

    // HU-10: menu de un restaurante, paginado y con filtro opcional por categoria
    // (idCategoria puede venir null = sin filtro)
    PaginaResponseDTO<PlatoResponseDTO> listarPlatos(Integer idRestaurante, Integer idCategoria, int pagina, int tamano);
}
