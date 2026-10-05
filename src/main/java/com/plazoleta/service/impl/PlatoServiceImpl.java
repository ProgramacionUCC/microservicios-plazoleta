package com.plazoleta.service.impl;

import com.plazoleta.dto.request.HabilitarPlatoRequestDTO;
import com.plazoleta.dto.request.ModificarPlatoRequestDTO;
import com.plazoleta.dto.request.PlatoRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reglas de negocio de platos: HU-03 (crear), HU-04 (modificar), HU-07 (habilitar/deshabilitar)
 * y HU-10 (listar el menu para el cliente).
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
     * HU-07: habilitar/deshabilitar plato. Solo cambia el estado; lo demas no se toca.
     * El campo activo del DTO se guarda en Plato.estado.
     */
    @Override
    public PlatoResponseDTO cambiarEstadoPlato(Integer idPlato, HabilitarPlatoRequestDTO dto, String correoUsuario) {
        // 1. El plato debe existir (si no, 404). Se usa ResponseStatusException para no
        // tocar el GlobalExceptionHandler ni cambiar el 400 de las otras HU.
        Plato plato = platoRepository.findById(idPlato)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El plato no existe"));

        // 2. "No se permite modificar platos de otros restaurantes diferentes al propio"
        validarDueno(plato.getRestaurante(), correoUsuario);

        // 3. Se cambia SOLO el estado (true = activo, false = inactivo)
        plato.setEstado(dto.getActivo());

        // 4. save() sobre un plato que ya tiene id hace UPDATE (no crea uno nuevo)
        return convertir(platoRepository.save(plato));
    }

    /**
     * HU-10: menu de un restaurante para el cliente.
     * "Se deben listar paginados y poderse filtrar por categoria los platos de cada menu"
     * "Se debe poder paginar de acuerdo con el numero de elementos por pagina escogido"
     */
    @Override
    public PaginaResponseDTO<PlatoResponseDTO> listarPlatos(Integer idRestaurante, Integer idCategoria, int pagina, int tamano) {
        // 1. Los datos de la pagina deben tener sentido
        if (pagina < 0) {
            throw new ReglaNegocioException("La pagina debe ser 0 o mayor");
        }
        if (tamano < 1) {
            throw new ReglaNegocioException("El tamano de pagina debe ser 1 o mayor");
        }

        // 2. El restaurante debe existir
        if (!restauranteRepository.existsById(idRestaurante)) {
            throw new ReglaNegocioException("El restaurante no existe");
        }

        // 3. PageRequest dice que pagina y cuantos platos traer.
        //    Se ordena por id para que las paginas siempre salgan en el mismo orden.
        Pageable paginacion = PageRequest.of(pagina, tamano, Sort.by("id"));

        // 4. Solo platos activos (HU-07: desactivar = dejar de ofrecerlo en el menu).
        //    Si llega idCategoria se filtra; si no, se traen todas las categorias.
        Page<Plato> platos;
        if (idCategoria == null) {
            platos = platoRepository.findByRestauranteIdAndEstadoTrue(idRestaurante, paginacion);
        } else {
            platos = platoRepository.findByRestauranteIdAndCategoriaIdAndEstadoTrue(idRestaurante, idCategoria, paginacion);
        }

        // 5. Se arma la respuesta: los platos de esta pagina + los totales
        return PaginaResponseDTO.<PlatoResponseDTO>builder()
                .contenido(platos.getContent().stream().map(this::convertir).toList())
                .pagina(platos.getNumber())
                .tamano(platos.getSize())
                .totalElementos(platos.getTotalElements())
                .totalPaginas(platos.getTotalPages())
                .build();
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

    // Convierte la entity Plato en el DTO de respuesta (lo usan crear, modificar y cambiar estado)
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
