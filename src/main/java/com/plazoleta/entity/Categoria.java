package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

// Tabla categoria: cada plato pertenece a una (HU-03)
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
