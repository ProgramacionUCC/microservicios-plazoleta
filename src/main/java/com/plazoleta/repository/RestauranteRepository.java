package com.plazoleta.repository;

import com.plazoleta.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guarda y busca restaurantes en la tabla "restaurante".
 * findById(id) y save(restaurante) ya vienen de JpaRepository.
 */
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {

    // HU-02: ¿ya existe un restaurante con este NIT? -> true / false
    boolean existsByNit(String nit);
}
