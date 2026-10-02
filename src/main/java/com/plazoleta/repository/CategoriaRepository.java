package com.plazoleta.repository;

import com.plazoleta.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guarda y busca categorias. Con lo que trae JpaRepository es suficiente:
 * save(), findById() y findAll().
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
