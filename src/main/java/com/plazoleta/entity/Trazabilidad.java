package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tabla "trazabilidad" (HU-17).
 * Guarda UN registro por cada cambio de estado de un pedido.
 *
 * Ej: pedido 5 cancelado ->
 *   fila: pedido 5, estado CANCELADO, fecha 2026-11-13 10:05
 *
 * La columna "fecha" la usa la HU-18 para calcular los tiempos.
 */
@Entity
@Table(name = "trazabilidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trazabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // A que pedido pertenece. FK: idPedido
    @ManyToOne
    @JoinColumn(name = "idPedido")
    private Pedido pedido;

    // Estado en el que quedo el pedido: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO o CANCELADO
    private String estado;

    // Momento exacto del cambio de estado (la HU-18 calcula los tiempos con esto)
    private LocalDateTime fecha;
}