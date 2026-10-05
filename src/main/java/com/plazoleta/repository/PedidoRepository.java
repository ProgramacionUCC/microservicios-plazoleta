package com.plazoleta.repository;

import com.plazoleta.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Guarda y busca pedidos en la tabla "pedido".
 */
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    /*
     * HU-11: ¿el cliente ya tiene un pedido en alguno de estos estados?
     * Spring arma la consulta con el nombre del metodo:
     *   existsBy ClienteId     -> WHERE idCliente = ?
     *   And EstadoIn           -> AND estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO')
     * Devuelve true o false.
     */
    boolean existsByClienteIdAndEstadoIn(Integer idCliente, List<String> estados);

    /*
     * HU-12: pedidos de un restaurante con un estado, paginados.
     * Spring arma la consulta con el nombre del metodo:
     *   findBy RestauranteId -> WHERE idRestaurante = ?
     *   And Estado            -> AND estado = ?
     * Pageable dice que pagina y cuantos traer; Page devuelve esa pagina + los totales.
     */
    Page<Pedido> findByRestauranteIdAndEstado(Integer idRestaurante, String estado, Pageable pageable);
}
