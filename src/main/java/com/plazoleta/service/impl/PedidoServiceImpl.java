package com.plazoleta.service.impl;

import com.plazoleta.dto.request.PedidoRequestDTO;
import com.plazoleta.dto.request.PlatoPedidoRequestDTO;
import com.plazoleta.dto.response.PaginaResponseDTO;
import com.plazoleta.dto.response.PedidoResponseDTO;
import com.plazoleta.dto.response.PlatoPedidoResponseDTO;
import com.plazoleta.entity.EmpleadoRestaurante;
import com.plazoleta.entity.Pedido;
import com.plazoleta.entity.Plato;
import com.plazoleta.entity.PlatoPedido;
import com.plazoleta.entity.Restaurante;
import com.plazoleta.entity.Usuario;
import com.plazoleta.exception.AccesoDenegadoException;
import com.plazoleta.exception.ReglaNegocioException;
import com.plazoleta.repository.EmpleadoRestauranteRepository;
import com.plazoleta.repository.PedidoRepository;
import com.plazoleta.repository.PlatoPedidoRepository;
import com.plazoleta.repository.PlatoRepository;
import com.plazoleta.repository.RestauranteRepository;
import com.plazoleta.repository.UsuarioRepository;
import com.plazoleta.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de negocio de pedidos: HU-11 (realizar pedido) y HU-12 (listar por estado).
 * Que el usuario sea CLIENTE o EMPLEADO ya lo reviso SecurityConfig.
 * El formato (restaurante, lista de platos, cantidades) ya lo reviso el DTO.
 */
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    // Estados en los que un pedido todavia esta "en proceso" (HU-11)
    private static final List<String> ESTADOS_EN_PROCESO = List.of("PENDIENTE", "EN_PREPARACION", "LISTO");

    // Todos los estados validos para filtrar (HU-12)
    private static final List<String> ESTADOS_VALIDOS = List.of("PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO", "CANCELADO");

    private final PedidoRepository pedidoRepository;
    private final PlatoPedidoRepository platoPedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository restauranteRepository;
    private final PlatoRepository platoRepository;
    private final EmpleadoRestauranteRepository empleadoRestauranteRepository;  // Para saber de que restaurante es el empleado

    @Override
    public PedidoResponseDTO crearPedido(PedidoRequestDTO dto, String correoCliente) {
        // 1. El cliente es el usuario que hizo login (su correo viene del token)
        Usuario cliente = usuarioRepository.findByCorreo(correoCliente)
                .orElseThrow(() -> new ReglaNegocioException("El cliente no existe"));

        // 2. "El cliente puede solicitar un nuevo pedido, solo si no tiene
        //     ningun pedido en proceso (en_preparacion, pendiente o listo)"
        if (pedidoRepository.existsByClienteIdAndEstadoIn(cliente.getId(), ESTADOS_EN_PROCESO)) {
            throw new ReglaNegocioException("Ya tienes un pedido en proceso, no puedes hacer otro hasta que termine");
        }

        // 3. "Todo pedido debe especificar el restaurante": debe existir
        Restaurante restaurante = restauranteRepository.findById(dto.getIdRestaurante())
                .orElseThrow(() -> new ReglaNegocioException("El restaurante no existe"));

        // 4. Se revisan TODOS los platos antes de guardar nada.
        //    Asi, si uno esta mal, no queda un pedido guardado a medias.
        List<Plato> platosValidados = new ArrayList<>();
        for (PlatoPedidoRequestDTO item : dto.getPlatos()) {
            Plato plato = platoRepository.findById(item.getIdPlato())
                    .orElseThrow(() -> new ReglaNegocioException("El plato " + item.getIdPlato() + " no existe"));

            // "Un pedido consta de una lista de platos de un mismo restaurante"
            if (!plato.getRestaurante().getId().equals(restaurante.getId())) {
                throw new ReglaNegocioException("El plato " + plato.getNombre() + " no es del restaurante del pedido");
            }

            // HU-07: un plato desactivado ya no se ofrece, no se puede pedir
            if (!plato.getEstado()) {
                throw new ReglaNegocioException("El plato " + plato.getNombre() + " no esta disponible");
            }

            platosValidados.add(plato);
        }

        // 5. "Inmediatamente despues de recibir un pedido, este debe quedar con estado Pendiente"
        Pedido pedido = Pedido.builder()
                .estado("PENDIENTE")
                .cliente(cliente)
                .restaurante(restaurante)
                .build();
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 6. Se guarda cada plato con su cantidad (tabla plato_pedido)
        //    platosValidados y dto.getPlatos() estan en el mismo orden
        List<PlatoPedidoResponseDTO> platosRespuesta = new ArrayList<>();
        for (int i = 0; i < platosValidados.size(); i++) {
            Plato plato = platosValidados.get(i);
            Integer cantidad = dto.getPlatos().get(i).getCantidad();

            platoPedidoRepository.save(PlatoPedido.builder()
                    .pedido(pedidoGuardado)
                    .plato(plato)
                    .cantidad(cantidad)
                    .build());

            platosRespuesta.add(PlatoPedidoResponseDTO.builder()
                    .idPlato(plato.getId())
                    .nombre(plato.getNombre())
                    .cantidad(cantidad)
                    .build());
        }

        // 7. Se devuelve el pedido creado
        return PedidoResponseDTO.builder()
                .id(pedidoGuardado.getId())
                .estado(pedidoGuardado.getEstado())
                .idRestaurante(restaurante.getId())
                .platos(platosRespuesta)
                .build();
    }

    /**
     * HU-12: el empleado ve los pedidos de SU restaurante filtrados por estado.
     * "Solo se pueden listar los pedidos del restaurante al que pertenece el empleado"
     */
    @Override
    public PaginaResponseDTO<PedidoResponseDTO> listarPedidosPorEstado(String estado, int pagina, int tamano, String correoEmpleado) {
        // 1. El empleado es el usuario que hizo login (su correo viene del token)
        Usuario empleado = usuarioRepository.findByCorreo(correoEmpleado)
                .orElseThrow(() -> new ReglaNegocioException("El empleado no existe"));

        // 2. Se busca a que restaurante pertenece (tabla empleado_restaurante).
        //    Si no esta en ninguno, no puede ver pedidos -> 403.
        EmpleadoRestaurante vinculo = empleadoRestauranteRepository.findByEmpleadoId(empleado.getId())
                .orElseThrow(() -> new AccesoDenegadoException("Solo los empleados con restaurante asignado pueden listar pedidos"));

        // 3. El filtro debe ser un estado valido
        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            throw new ReglaNegocioException("El estado debe ser uno de: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO");
        }

        // 4. Los datos de la pagina deben tener sentido (igual que en HU-10)
        if (pagina < 0) {
            throw new ReglaNegocioException("La pagina debe ser 0 o mayor");
        }
        if (tamano < 1) {
            throw new ReglaNegocioException("El tamano de pagina debe ser 1 o mayor");
        }

        // 5. PageRequest dice que pagina y cuantos pedidos traer.
        //    Se ordena por id para que las paginas siempre salgan en el mismo orden.
        Pageable paginacion = PageRequest.of(pagina, tamano, Sort.by("id"));

        // 6. Solo pedidos de SU restaurante y con ese estado
        Page<Pedido> pedidos = pedidoRepository.findByRestauranteIdAndEstado(
                vinculo.getRestaurante().getId(), estado, paginacion);

        // 7. Se arma la respuesta: los pedidos de esta pagina (con todos sus campos) + los totales
        return PaginaResponseDTO.<PedidoResponseDTO>builder()
                .contenido(pedidos.getContent().stream().map(this::convertirConPlatos).toList())
                .pagina(pedidos.getNumber())
                .tamano(pedidos.getSize())
                .totalElementos(pedidos.getTotalElements())
                .totalPaginas(pedidos.getTotalPages())
                .build();
    }

    // Convierte un Pedido en su DTO con TODOS los campos: id, estado,
    // restaurante y la lista de platos con su cantidad (tabla plato_pedido)
    private PedidoResponseDTO convertirConPlatos(Pedido pedido) {
        List<PlatoPedidoResponseDTO> platos = platoPedidoRepository.findByPedidoId(pedido.getId()).stream()
                .map(pp -> PlatoPedidoResponseDTO.builder()
                        .idPlato(pp.getPlato().getId())
                        .nombre(pp.getPlato().getNombre())
                        .cantidad(pp.getCantidad())
                        .build())
                .toList();

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .estado(pedido.getEstado())
                .idRestaurante(pedido.getRestaurante().getId())
                .platos(platos)
                .build();
    }
}
