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

/**
 * Reglas de negocio de la HU-02 (crear restaurante).
 * El formato (nombre, NIT, telefono...) ya lo reviso el DTO.
 */
@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;  // Para buscar al propietario

    @Override
    public RestauranteResponseDTO crearRestaurante(RestauranteRequestDTO dto) {
        // 1. El NIT no se puede repetir (es UNIQUE en la tabla)
        if (restauranteRepository.existsByNit(dto.getNit())) {
            throw new ReglaNegocioException("El NIT ya esta registrado");
        }

        // 2. La HU dice: el id debe corresponder a un usuario con rol PROPIETARIO.
        //    findById devuelve un Optional: si no existe, orElseThrow lanza el error.
        Usuario propietario = usuarioRepository.findById(dto.getIdPropietario())
                .orElseThrow(() -> new ReglaNegocioException("El propietario no existe"));

        if (!propietario.getRol().getNombre().equals("PROPIETARIO")) {
            throw new ReglaNegocioException("El usuario no tiene rol PROPIETARIO");
        }

        // 3. Se arma la entity y se guarda en MySQL
        Restaurante restaurante = Restaurante.builder()
                .nombre(dto.getNombre())
                .nit(dto.getNit())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .urlLogo(dto.getUrlLogo())
                .propietario(propietario)  // Se guarda el objeto; JPA pone el idPropietario
                .build();

        Restaurante guardado = restauranteRepository.save(restaurante);

        // 4. Se devuelve el DTO de respuesta
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
