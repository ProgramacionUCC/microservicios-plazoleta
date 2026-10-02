package com.plazoleta.service.impl;

import com.plazoleta.dto.request.RestauranteRequestDTO;
import com.plazoleta.dto.response.RestauranteResponseDTO;
import com.plazoleta.entity.Restaurante;
import com.plazoleta.entity.Usuario;
import com.plazoleta.exception.ReglaNegocioException;
import com.plazoleta.repository.RestauranteRepository;
import com.plazoleta.repository.UsuarioRepository;
import com.plazoleta.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    // HU-02: el formato ya llega validado por el DTO
    @Override
    public RestauranteResponseDTO crearRestaurante(RestauranteRequestDTO dto) {
        // 1. El NIT no se puede repetir (UNIQUE en la tabla)
        if (restauranteRepository.existsByNit(dto.getNit())) {
            throw new ReglaNegocioException("El NIT ya esta registrado");
        }

        // 2. El id debe ser de un usuario que exista y tenga rol PROPIETARIO
        Usuario propietario = usuarioRepository.findById(dto.getIdPropietario())
                .orElseThrow(() -> new ReglaNegocioException("El propietario no existe"));

        if (!propietario.getRol().getNombre().equals("PROPIETARIO")) {
            throw new ReglaNegocioException("El usuario no tiene rol PROPIETARIO");
        }

        // 3. Se guarda el restaurante
        Restaurante restaurante = Restaurante.builder()
                .nombre(dto.getNombre())
                .nit(dto.getNit())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .urlLogo(dto.getUrlLogo())
                .propietario(propietario)
                .build();

        Restaurante guardado = restauranteRepository.save(restaurante);

        return RestauranteResponseDTO.builder()
                .id(guardado.getId())
                .nombre(guardado.getNombre())
                .nit(guardado.getNit())
                .direccion(guardado.getDireccion())
                .telefono(guardado.getTelefono())
                .urlLogo(guardado.getUrlLogo())
                .idPropietario(propietario.getId())
                .build();
    }
}
