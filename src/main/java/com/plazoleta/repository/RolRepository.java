package com.plazoleta.repository;

import com.plazoleta.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * REPOSITORY = la clase que guarda y busca en la base de datos.
 *
 * Al heredar de JpaRepository<Rol, Integer> (entity Rol, id de tipo Integer)
 * Spring nos regala sin escribir nada: save(), findById(), findAll(), deleteById()...
 *
 * Es una interfaz: no escribimos el codigo, Spring lo crea solo.
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Spring entiende el nombre del metodo y arma la consulta:
    // SELECT * FROM rol WHERE nombre = ?
    // Optional = puede que encuentre el rol o puede que no (evita null)
    Optional<Rol> findByNombre(String nombre);
}
