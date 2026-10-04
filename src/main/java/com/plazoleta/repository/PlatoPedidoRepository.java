package com.plazoleta.repository;

import com.plazoleta.entity.PlatoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guarda los platos de cada pedido (tabla "plato_pedido").
 * Con save() que trae JpaRepository es suficiente.
 */
public interface PlatoPedidoRepository extends JpaRepository<PlatoPedido, Integer> {
}
