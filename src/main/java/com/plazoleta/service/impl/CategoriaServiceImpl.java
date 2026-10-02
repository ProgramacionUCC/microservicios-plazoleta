package com.plazoleta.service.impl;

import com.plazoleta.dto.request.CategoriaRequestDTO;
import com.plazoleta.dto.response.CategoriaResponseDTO;
import com.plazoleta.entity.Categoria;
import com.plazoleta.repository.CategoriaRepository;
import com.plazoleta.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    private CategoriaResponseDTO convertir(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}
