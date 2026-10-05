package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla "plato_pedido" (HU-11).
 * Cada fila es UN plato dentro de UN pedido, con su cantidad.
 *
 * Ej: pedido 5 con 2 hamburguesas y 1 limonada =
 *   fila 1: pedido 5, plato hamburguesa, cantidad 2
 *   fila 2: pedido 5, plato limonada,    cantidad 1
 */
@Entity
@Table(name = "plato_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // "la cantidad de cada uno de esos platos" (HU-11)
    private Integer cantidad;

    // A que pedido pertenece. FK: idPedido
    @ManyToOne
    @JoinColumn(name = "idPedido")
    private Pedido pedido;

    // Que plato es. FK: idPlato
    @ManyToOne
    @JoinColumn(name = "idPlato")
    private Plato plato;
}
