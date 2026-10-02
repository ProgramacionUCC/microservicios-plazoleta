package com.plazoleta.service;

import com.plazoleta.dto.request.CategoriaRequestDTO;
import com.plazoleta.dto.response.CategoriaResponseDTO;

import java.util.List;

/**
 * Que se puede hacer con categorias (necesarias para crear platos en la HU-03).
 */
public interface CategoriaService {
    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaRequestDTO);
    List<CategoriaResponseDTO> listarCategorias();
}
