package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Tabla "usuario": guarda a TODAS las personas del sistema
 * (administrador, propietario, empleado y cliente).
 * Lo unico que cambia entre ellos es el rol.
 *
 * En Java puro teniamos clases separadas (Propietario, Empleado).
 * Aqui es una sola, igual que en el diagrama y en el script de la profe.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Cada atributo se conecta con la columna del mismo nombre en MySQL
    private String nombre;
    private String apellido;
    private String documentoDeIdentidad;  // String para conservar ceros al inicio
    private String celular;               // String porque puede tener el simbolo +
    private LocalDate fechaDeNacimiento;  // Solo la pide la HU-01 (propietario)
    private String correo;                // Se usa para el login (HU-05)
    private String clave;                 // Siempre encriptada con bcrypt, nunca en texto normal

    /*
     * Relacion con la tabla rol:
     *  - @ManyToOne: MUCHOS usuarios tienen UN mismo rol.
     *  - @JoinColumn(name = "idRol"): la columna idRol de la tabla usuario
     *    es la llave foranea (FK) que apunta al id de la tabla rol.
     * Gracias a esto podemos hacer usuario.getRol().getNombre() sin escribir SQL.
     */
    @ManyToOne
    @JoinColumn(name = "idRol")
    private Rol rol;
}
