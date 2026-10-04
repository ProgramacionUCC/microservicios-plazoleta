package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla "pedido" (HU-11 realizar pedido).
 *
 * Por ahora solo tiene lo que usa la HU-11: estado, cliente y restaurante.
 * La tabla tambien tiene idEmpleado y pin; esos campos los agregan
 * las HU que los usan (HU-12, HU-13, HU-14...).
 */
@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO o CANCELADO.
    // Todo pedido nuevo nace en PENDIENTE (HU-11).
    private String estado;

    // MUCHOS pedidos pueden ser de UN mismo cliente. FK: idCliente
    @ManyToOne
    @JoinColumn(name = "idCliente")
    private Usuario cliente;

    // MUCHOS pedidos pueden ser de UN mismo restaurante. FK: idRestaurante
    // "Todo pedido debe especificar el restaurante" (HU-11)
    @ManyToOne
    @JoinColumn(name = "idRestaurante")
    private Restaurante restaurante;
}
