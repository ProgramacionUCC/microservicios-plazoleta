package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla "empleado_restaurante" (HU-06).
 * Guarda a que restaurante pertenece cada empleado.
 *
 * El empleado es un Usuario con rol EMPLEADO (tabla usuario).
 * Esta tabla solo los une con su restaurante. La HU-12 la usa para que
 * cada empleado vea solo los pedidos de su restaurante.
 */
@Entity
@Table(name = "empleado_restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoRestaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /*
     * @OneToOne: UN empleado pertenece a UN solo restaurante
     * (en la tabla, idEmpleado es UNIQUE: no se puede repetir).
     * La HU-12 habla de "el restaurante al que pertenece el empleado", en singular.
     */
    @OneToOne
    @JoinColumn(name = "idEmpleado")
    private Usuario empleado;

    // MUCHOS empleados pueden trabajar en UN mismo restaurante
    @ManyToOne
    @JoinColumn(name = "idRestaurante")
    private Restaurante restaurante;
}
