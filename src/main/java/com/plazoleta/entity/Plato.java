package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla "plato" (HU-03 crear, HU-04 modificar, HU-07 habilitar/deshabilitar).
 */
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
    private Integer precio;      // Entero mayor a 0 (HU-03)
    private String descripcion;  // Se puede modificar (HU-04)
    private String urlImagen;
    private Boolean estado;      // true = activo. Todo plato nuevo nace en true (HU-03)

    // MUCHOS platos pertenecen a UNA categoria. FK: idCategoria
    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    // MUCHOS platos pertenecen a UN restaurante. FK: idRestaurante
    // "Todo plato debe estar asociado a un restaurante" (HU-03)
    @ManyToOne
    @JoinColumn(name = "idRestaurante")
    private Restaurante restaurante;
}
