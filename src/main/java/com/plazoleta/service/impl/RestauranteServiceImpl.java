package com.plazoleta.service.impl;

import com.plazoleta.dto.request.RestauranteRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
import com.plazoleta.dto.response.RestauranteListadoResponseDTO;
import com.plazoleta.dto.response.RestauranteResponseDTO;
import com.plazoleta.entity.Restaurante;
import com.plazoleta.entity.Usuario;
import com.plazoleta.exception.ReglaNegocioException;
import com.plazoleta.repository.RestauranteRepository;
import com.plazoleta.repository.UsuarioRepository;
import com.plazoleta.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio de restaurantes: HU-02 (crear) y HU-09 (listar para el cliente).
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

    /**
     * HU-09: restaurantes para el cliente, por orden alfabetico y paginados.
     * "Se deben listar todos los restaurantes por orden alfabetico y paginados
     * de acuerdo con un campo que permita especificar cuantos elementos por pagina"
     */
    @Override
    public PaginaResponseDTO<RestauranteListadoResponseDTO> listarRestaurantes(int pagina, int tamano) {
        // 1. Los datos de la pagina deben tener sentido (igual que en HU-10)
        if (pagina < 0) {
            throw new ReglaNegocioException("La pagina debe ser 0 o mayor");
        }
        if (tamano < 1) {
            throw new ReglaNegocioException("El tamano de pagina debe ser 1 o mayor");
        }

        // 2. PageRequest dice que pagina y cuantos restaurantes traer.
        //    Se ordena por nombre para que salgan de la A a la Z.
        Pageable paginacion = PageRequest.of(pagina, tamano, Sort.by("nombre").ascending());

        // 3. findAll(Pageable) ya viene de JpaRepository: trae solo esa pagina + los totales
        Page<Restaurante> restaurantes = restauranteRepository.findAll(paginacion);

        // 4. Se arma la respuesta: solo nombre + urlLogo por restaurante, mas los totales
        return PaginaResponseDTO.<RestauranteListadoResponseDTO>builder()
                .contenido(restaurantes.getContent().stream()
                        .map(r -> RestauranteListadoResponseDTO.builder()
                                .nombre(r.getNombre())
                                .urlLogo(r.getUrlLogo())
                                .build())
                        .toList())
                .pagina(restaurantes.getNumber())
                .tamano(restaurantes.getSize())
                .totalElementos(restaurantes.getTotalElements())
                .totalPaginas(restaurantes.getTotalPages())
                .build();
    }
}
