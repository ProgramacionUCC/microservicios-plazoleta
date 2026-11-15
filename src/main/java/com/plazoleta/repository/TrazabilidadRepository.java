package com.plazoleta.repository;

import com.plazoleta.entity.Trazabilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Guarda los cambios de estado de los pedidos (tabla "trazabilidad"). HU-17.
 * Con save() que trae JpaRepository se guarda un registro por cada cambio.
 */
public interface TrazabilidadRepository extends JpaRepository<Trazabilidad, Integer> {

    /*
     * HU-17: historial de un pedido, del mas viejo al mas nuevo.
     * Spring arma la consulta con el nombre del metodo:
     *   findBy PedidoId -> WHERE idPedido = ?
     *   OrderByIdAsc    -> ORDER BY id ASC
     */
    List<Trazabilidad> findByPedidoIdOrderByIdAsc(Integer idPedido);
}