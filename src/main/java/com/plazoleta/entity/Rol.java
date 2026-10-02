package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ENTITY = una clase que representa una tabla de la base de datos.
 * Cada objeto Rol es una fila de la tabla "rol".
 *
 * Los roles ya vienen creados en docs/script.sql:
 *  1 ADMINISTRADOR, 2 PROPIETARIO, 3 EMPLEADO, 4 CLIENTE
 */
@Entity                 // Le dice a Spring que esta clase es una tabla
@Table(name = "rol")    // Nombre exacto de la tabla en MySQL
@Getter                 // Lombok: crea los getters (getId, getNombre...)
@Setter                 // Lombok: crea los setters
@NoArgsConstructor      // Lombok: constructor vacio (lo necesita JPA)
@AllArgsConstructor     // Lombok: constructor con todos los campos
@Builder                // Lombok: permite crear objetos asi -> Rol.builder().nombre("X").build()
public class Rol {

    @Id                                                  // Llave primaria (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // MySQL genera el id solo (AUTO_INCREMENT)
    private Integer id;

    private String nombre;       // ADMINISTRADOR, PROPIETARIO, EMPLEADO o CLIENTE
    private String descripcion;
}
