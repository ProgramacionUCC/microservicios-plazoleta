package com.plazoleta.service.impl;

import com.plazoleta.dto.request.CategoriaRequestDTO;
import com.plazoleta.dto.response.CategoriaResponseDTO;
import com.plazoleta.entity.Categoria;
import com.plazoleta.repository.CategoriaRepository;
import com.plazoleta.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Crear y listar categorias. No tiene reglas especiales:
 * solo guarda y consulta (igual que en el proyecto de la profe).
 */
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();

        return convertir(categoriaRepository.save(categoria));
    }

    @Override
    public List<CategoriaResponseDTO> listarCategorias() {
        // findAll() trae todas las filas de la tabla.
        // stream().map(...) convierte cada Categoria en un CategoriaResponseDTO.
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    // Convierte la entity en el DTO de respuesta (se usa en los dos metodos)
    private CategoriaResponseDTO convertir(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}
