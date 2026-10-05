package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla "categoria". La HU-03 pide que cada plato tenga una categoria
 * (ej: Hamburguesas, Bebidas). Igual que en el proyecto de la profe,
 * se crean con su propio endpoint (CategoriaController).
 */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String descripcion;
}
