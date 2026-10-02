package com.plazoleta.service.impl;

import com.plazoleta.dto.request.ModificarPlatoRequestDTO;
import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PlatoResponseDTO;
import com.plazoleta.entity.Categoria;
import com.plazoleta.entity.Plato;
import com.plazoleta.entity.Restaurante;
import com.plazoleta.exception.AccesoDenegadoException;
import com.plazoleta.exception.ReglaNegocioException;
import com.plazoleta.repository.CategoriaRepository;
import com.plazoleta.repository.PlatoRepository;
import com.plazoleta.repository.RestauranteRepository;
import com.plazoleta.service.PlatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio de platos: HU-03 (crear) y HU-04 (modificar).
 * El formato ya lo reviso el DTO; que el usuario tenga rol PROPIETARIO
 * ya lo reviso SecurityConfig. Aqui se revisa que sea EL DUEÑO.
 */
@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepository platoRepository;
    private final RestauranteRepository restauranteRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * HU-03: crear plato.
     */
    @Override
    public PlatoResponseDTO crearPlato(PlatoRequestDTO dto, String correoUsuario) {
        // 1. El restaurante y la categoria deben existir
        Restaurante restaurante = restauranteRepository.findById(dto.getIdRestaurante())
                .orElseThrow(() -> new ReglaNegocioException("El restaurante no existe"));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ReglaNegocioException("La categoria no existe"));

        // 2. "Solo el propietario de un restaurante puede crear platos"
        validarDueno(restaurante, correoUsuario);

        // 3. "Por defecto, cada plato recien creado tiene la variable activa en true"
        Plato plato = Plato.builder()
                .nombre(dto.getNombre())
                .precio(dto.getPrecio())
                .descripcion(dto.getDescripcion())
                .urlImagen(dto.getUrlImagen())
                .estado(true)
                .categoria(categoria)
                .restaurante(restaurante)
                .build();

        // 4. Se guarda y se devuelve como DTO
        return convertir(platoRepository.save(plato));
    }

    /**
     * HU-04: modificar plato. Solo cambia precio y descripcion; lo demas no se toca.
     */
    @Override
    public PlatoResponseDTO modificarPlato(Integer idPlato, ModificarPlatoRequestDTO dto, String correoUsuario) {
        // 1. El plato debe existir
        Plato plato = platoRepository.findById(idPlato)
                .orElseThrow(() -> new ReglaNegocioException("El plato no existe"));

        // 2. "No se permiten modificar platos de otros restaurantes diferentes al propio"
        validarDueno(plato.getRestaurante(), correoUsuario);

        // 3. Se cambian SOLO los dos campos permitidos
        plato.setPrecio(dto.getPrecio());
        plato.setDescripcion(dto.getDescripcion());

        // 4. save() sobre un plato que ya tiene id hace UPDATE (no crea uno nuevo)
        return convertir(platoRepository.save(plato));
    }

    /**
     * Revisa que el usuario logueado sea el dueño del restaurante.
     * Compara el correo del token con el correo del propietario del restaurante.
     * Si no es el dueño -> 403 (AccesoDenegadoException).
     */
    private void validarDueno(Restaurante restaurante, String correoUsuario) {
        if (!restaurante.getPropietario().getCorreo().equals(correoUsuario)) {
            throw new AccesoDenegadoException("Solo el propietario del restaurante puede gestionar sus platos");
        }
    }

    // Convierte la entity Plato en el DTO de respuesta (lo usan crear y modificar)
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
