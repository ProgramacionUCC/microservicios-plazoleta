package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

// Tabla rol: ADMINISTRADOR, PROPIETARIO, EMPLEADO, CLIENTE
@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String descripcion;
}
