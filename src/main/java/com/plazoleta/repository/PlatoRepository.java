package com.plazoleta.repository;

import com.plazoleta.entity.Plato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guarda y busca platos.
 * save() y findById() ya vienen de JpaRepository (HU-03, HU-04, HU-07).
 */
public interface PlatoRepository extends JpaRepository<Plato, Integer> {

    /*
     * HU-10: menu de un restaurante, paginado.
     * Spring arma la consulta con el nombre del metodo:
     *   findBy RestauranteId      -> WHERE idRestaurante = ?
     *   And EstadoTrue            -> AND estado = true (solo platos activos, HU-07)
     * Pageable dice que pagina y cuantos traer; Page devuelve esa pagina + los totales.
     */
    Page<Plato> findByRestauranteIdAndEstadoTrue(Integer idRestaurante, Pageable pageable);

    // HU-10 con filtro por categoria: ... AND idCategoria = ?
    Page<Plato> findByRestauranteIdAndCategoriaIdAndEstadoTrue(Integer idRestaurante, Integer idCategoria, Pageable pageable);
}
