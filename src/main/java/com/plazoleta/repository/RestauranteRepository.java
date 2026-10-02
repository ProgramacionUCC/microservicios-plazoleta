package com.plazoleta.repository;

import com.plazoleta.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    boolean existsByNit(String nit);
}
