package com.plazoleta.repository;

import com.plazoleta.entity.Plato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatoRepository extends JpaRepository<Plato, Integer> {
}
