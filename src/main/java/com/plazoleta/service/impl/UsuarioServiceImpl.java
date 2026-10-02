package com.plazoleta.service.impl;

import com.plazoleta.dto.request.PropietarioRequestDTO;
import com.plazoleta.dto.response.UsuarioResponseDTO;
import com.plazoleta.entity.Rol;
import com.plazoleta.entity.Usuario;
import com.plazoleta.exception.ReglaNegocioException;
import com.plazoleta.repository.RolRepository;
import com.plazoleta.repository.UsuarioRepository;
import com.plazoleta.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * SERVICE = el "cerebro". Aqui van las reglas de negocio de la HU.
 *
 * Cuando el codigo llega aqui, el formato ya fue revisado por el DTO.
 * Este service revisa lo que el DTO no puede: lo que depende de la base
 * de datos (correo repetido) o de la fecha actual (mayor de edad).
 */
@Service                   // Spring crea un objeto de esta clase y lo inyecta donde se necesite
@RequiredArgsConstructor   // Lombok: crea un constructor con todos los atributos "final".
                           // Spring usa ese constructor para pasarnos los repositories
                           // (esto se llama inyeccion de dependencias: no hacemos "new").
public class UsuarioServiceImpl implements UsuarioService {

    private static final int EDAD_MINIMA = 18;

    private final UsuarioRepository usuarioRepository;  // Para guardar y buscar usuarios
    private final RolRepository rolRepository;          // Para buscar el rol PROPIETARIO
    private final PasswordEncoder passwordEncoder;      // Bcrypt (viene de SecurityConfig)

    /**
     * HU-01: crear propietario.
     * Lanza ReglaNegocioException si no se cumple alguna regla;
     * el GlobalExceptionHandler la convierte en una respuesta 400.
     */
    @Override
    public UsuarioResponseDTO crearPropietario(PropietarioRequestDTO dto) {
        // 1. Correo y documento no se pueden repetir (son UNIQUE en la tabla)
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new ReglaNegocioException("El correo ya esta registrado");
        }
        if (usuarioRepository.existsByDocumentoDeIdentidad(dto.getDocumentoDeIdentidad())) {
            throw new ReglaNegocioException("El documento ya esta registrado");
        }

        // 2. Debe ser mayor de edad.
        //    Period.between calcula los años entre la fecha de nacimiento y hoy.
        int edad = Period.between(dto.getFechaDeNacimiento(), LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new ReglaNegocioException("El propietario debe ser mayor de edad");
        }

        // 3. Queda con el rol PROPIETARIO (se busca en la tabla rol)
        Rol rolPropietario = rolRepository.findByNombre("PROPIETARIO")
                .orElseThrow(() -> new ReglaNegocioException("No existe el rol PROPIETARIO"));

        // 4. Se arma la entity con la clave encriptada.
        //    passwordEncoder.encode("clave123") -> "$2a$10$..." (no se puede revertir)
        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .documentoDeIdentidad(dto.getDocumentoDeIdentidad())
                .celular(dto.getCelular())
                .fechaDeNacimiento(dto.getFechaDeNacimiento())
                .correo(dto.getCorreo())
                .clave(passwordEncoder.encode(dto.getClave()))
                .rol(rolPropietario)
                .build();

        // 5. Se guarda en MySQL. save() devuelve el usuario ya con su id generado.
        Usuario guardado = usuarioRepository.save(usuario);

        // 6. Se convierte la entity en el DTO de respuesta (sin la clave)
        return UsuarioResponseDTO.builder()
                .id(guardado.getId())
                .nombre(guardado.getNombre())
                .apellido(guardado.getApellido())
                .documentoDeIdentidad(guardado.getDocumentoDeIdentidad())
                .celular(guardado.getCelular())
                .correo(guardado.getCorreo())
                .rol(guardado.getRol().getNombre())
                .build();
    }
}
