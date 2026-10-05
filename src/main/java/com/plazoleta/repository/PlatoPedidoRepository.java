package com.plazoleta.repository;

import com.plazoleta.entity.PlatoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Guarda los platos de cada pedido (tabla "plato_pedido").
 * Con save() que trae JpaRepository es suficiente (HU-11).
 * En la HU-12 se usa para mostrar los platos de cada pedido listado.
 */
public interface PlatoPedidoRepository extends JpaRepository<PlatoPedido, Integer> {

    /*
     * HU-12: platos de un pedido (para listar "todos los campos del pedido").
     *   findBy PedidoId -> WHERE idPedido = ?
     */
    List<PlatoPedido> findByPedidoId(Integer idPedido);
}
