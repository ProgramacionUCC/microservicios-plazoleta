package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

// Tabla usuario: guarda a todos (admin, propietario, empleado y cliente).
// Lo que cambia entre ellos es el rol.
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

    private String nombre;
    private String apellido;
    private String documentoDeIdentidad;
    private String celular;
    private LocalDate fechaDeNacimiento;
    private String correo;
    private String clave; // siempre encriptada con bcrypt

    @ManyToOne
    @JoinColumn(name = "idRol")
    private Rol rol;
}
