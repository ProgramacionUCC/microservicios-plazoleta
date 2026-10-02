package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

// Tabla restaurante (HU-02)
@Entity
@Table(name = "restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String nit;
    private String direccion;
    private String telefono;
    private String urlLogo;

    // Dueño del restaurante: un usuario con rol PROPIETARIO
    @ManyToOne
    @JoinColumn(name = "idPropietario")
    private Usuario propietario;
}
