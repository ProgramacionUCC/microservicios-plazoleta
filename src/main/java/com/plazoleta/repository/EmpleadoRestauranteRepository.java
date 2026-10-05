package com.plazoleta.repository;

import com.plazoleta.entity.EmpleadoRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Guarda y busca en la tabla "empleado_restaurante" (HU-06).
 * En la HU-12 se usa para buscar el restaurante de un empleado.
 */
public interface EmpleadoRestauranteRepository extends JpaRepository<EmpleadoRestaurante, Integer> {

    /*
     * HU-12: ¿a que restaurante pertenece este empleado?
     * Spring arma la consulta con el nombre del metodo:
     *   findBy EmpleadoId -> WHERE idEmpleado = ? (es UNIQUE: sale uno o ninguno)
     */
    Optional<EmpleadoRestaurante> findByEmpleadoId(Integer idEmpleado);
}
