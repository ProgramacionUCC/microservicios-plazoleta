package com.plazoleta.repository;

import com.plazoleta.entity.Pedido;
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
}
