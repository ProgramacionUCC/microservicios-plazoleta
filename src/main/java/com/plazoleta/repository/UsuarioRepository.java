package com.plazoleta.repository;

import com.plazoleta.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Guarda y busca usuarios en la tabla "usuario".
 * Los metodos se escriben solo con el nombre; Spring arma el SQL.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // HU-05 login: SELECT * FROM usuario WHERE correo = ?
    Optional<Usuario> findByCorreo(String correo);

    // HU-01: ¿ya hay un usuario con este correo? -> true / false
    boolean existsByCorreo(String correo);

    // HU-01: ¿ya hay un usuario con este documento? -> true / false
    boolean existsByDocumentoDeIdentidad(String documentoDeIdentidad);
}
