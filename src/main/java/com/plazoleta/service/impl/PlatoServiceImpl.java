package com.plazoleta.service.impl;

import com.plazoleta.dto.request.ModificarPlatoRequestDTO;
import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PlatoResponseDTO;
import com.plazoleta.entity.Categoria;
import com.plazoleta.entity.Plato;
import com.plazoleta.entity.Restaurante;
import com.plazoleta.exception.ReglaNegocioException;
import com.plazoleta.repository.CategoriaRepository;
import com.plazoleta.repository.PlatoRepository;
import com.plazoleta.repository.RestauranteRepository;
import com.plazoleta.service.PlatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepository platoRepository;
    private final RestauranteRepository restauranteRepository;
    private final CategoriaRepository categoriaRepository;

    // HU-03: el formato ya llega validado por el DTO
    @Override
    public PlatoResponseDTO crearPlato(PlatoRequestDTO dto) {
        // 1. El restaurante y la categoria deben existir
        Restaurante restaurante = restauranteRepository.findById(dto.getIdRestaurante())
                .orElseThrow(() -> new ReglaNegocioException("El restaurante no existe"));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ReglaNegocioException("La categoria no existe"));

        // 2. Solo el propietario de ese restaurante puede crear platos
        validarDueno(restaurante, dto.getIdPropietario());

        // 3. Todo plato nuevo nace activo
        Plato plato = Plato.builder()
                .nombre(dto.getNombre())
                .precio(dto.getPrecio())
                .descripcion(dto.getDescripcion())
                .urlImagen(dto.getUrlImagen())
                .estado(true)
                .categoria(categoria)
                .restaurante(restaurante)
                .build();

        return convertir(platoRepository.save(plato));
    }

    // HU-04: solo cambia precio y descripcion, lo demas no se toca
    @Override
    public PlatoResponseDTO modificarPlato(Integer idPlato, ModificarPlatoRequestDTO dto) {
        // 1. El plato debe existir
        Plato plato = platoRepository.findById(idPlato)
                .orElseThrow(() -> new ReglaNegocioException("El plato no existe"));

        // 2. No se pueden modificar platos de otro restaurante
        validarDueno(plato.getRestaurante(), dto.getIdPropietario());

        // 3. Se cambian solo los dos campos permitidos
        plato.setPrecio(dto.getPrecio());
        plato.setDescripcion(dto.getDescripcion());

        return convertir(platoRepository.save(plato));
    }

    private void validarDueno(Restaurante restaurante, Integer idPropietario) {
        if (!restaurante.getPropietario().getId().equals(idPropietario)) {
            throw new ReglaNegocioException("Solo el propietario del restaurante puede gestionar sus platos");
        }
    }

    private PlatoResponseDTO convertir(Plato plato) {
        return PlatoResponseDTO.builder()
                .id(plato.getId())
                .nombre(plato.getNombre())
                .precio(plato.getPrecio())
                .descripcion(plato.getDescripcion())
                .urlImagen(plato.getUrlImagen())
                .estado(plato.getEstado())
                .categoria(plato.getCategoria().getNombre())
                .idRestaurante(plato.getRestaurante().getId())
                .build();
    }
}
