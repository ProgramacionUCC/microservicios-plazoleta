package com.plazoleta.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla "restaurante" (HU-02).
 * Cada restaurante pertenece a un usuario con rol PROPIETARIO.
 */
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
    private String nit;        // Solo numeros (se valida en el DTO), UNIQUE en la tabla
    private String direccion;
    private String telefono;   // Maximo 13, puede empezar con +
    private String urlLogo;

    /*
     * Dueño del restaurante.
     * @ManyToOne: UN propietario puede tener MUCHOS restaurantes.
     * La columna idPropietario de la tabla es la FK hacia usuario.id.
     * En Java se guarda el objeto Usuario completo, no solo el numero,
     * asi podemos hacer restaurante.getPropietario().getCorreo().
     */
    @ManyToOne
    @JoinColumn(name = "idPropietario")
    private Usuario propietario;
}
