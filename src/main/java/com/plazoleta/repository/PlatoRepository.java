package com.plazoleta.repository;

import com.plazoleta.entity.Plato;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guarda y busca platos. Por ahora basta con save() y findById().
 * En la HU-10 (listar platos por restaurante y categoria) se agregan
 * metodos nuevos aqui.
 */
public interface PlatoRepository extends JpaRepository<Plato, Integer> {
}
