package com.plazoleta.repository;

import com.plazoleta.entity.Trazabilidad;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guarda los cambios de estado de los pedidos (tabla "trazabilidad"). HU-17.
 * Por ahora solo se usa save(), que ya trae JpaRepository.
 */
public interface TrazabilidadRepository extends JpaRepository<Trazabilidad, Integer> {
}