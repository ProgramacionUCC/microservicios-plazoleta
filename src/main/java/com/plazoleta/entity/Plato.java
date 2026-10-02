package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

// Tabla plato (HU-03, HU-04)
@Entity
@Table(name = "plato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private Integer precio;
    private String descripcion;
    private String urlImagen;
    private Boolean estado; // true = activo

    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "idRestaurante")
    private Restaurante restaurante;
}
