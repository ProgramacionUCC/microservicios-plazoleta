package com.plazoleta.repository;

import com.plazoleta.entity.EmpleadoRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guarda y busca en la tabla "empleado_restaurante" (HU-06).
 * Por ahora basta con save(). En la HU-12 se agrega un metodo
 * para buscar el restaurante de un empleado.
 */
public interface EmpleadoRestauranteRepository extends JpaRepository<EmpleadoRestaurante, Integer> {
}
