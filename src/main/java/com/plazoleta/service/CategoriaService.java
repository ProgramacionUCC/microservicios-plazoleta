package com.plazoleta.service;

import com.plazoleta.dto.request.CategoriaRequestDTO;
import com.plazoleta.dto.response.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {
    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaRequestDTO);
    List<CategoriaResponseDTO> listarCategorias();
}
