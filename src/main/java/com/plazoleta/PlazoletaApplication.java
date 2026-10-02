package com.plazoleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de arranque del proyecto. Reemplaza al Main.java de Java puro.
 *
 * Al darle Run, Spring Boot:
 *  1. Busca todas las clases de este paquete (com.plazoleta) y sus subcarpetas
 *     que tengan anotaciones como @RestController, @Service, @Repository o @Configuration.
 *  2. Crea un objeto de cada una y los conecta entre si (inyeccion de dependencias).
 *  3. Se conecta a MySQL con los datos de application.properties.
 *  4. Levanta un servidor web en http://localhost:8080 para recibir peticiones (Postman).
 */
@SpringBootApplication // Activa la configuracion automatica de Spring Boot
public class PlazoletaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlazoletaApplication.class, args);
    }
}
