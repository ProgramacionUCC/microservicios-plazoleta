package com.plazoleta.service.impl;

import com.plazoleta.dto.request.ClienteRequestDTO;
import com.plazoleta.dto.request.EmpleadoRequestDTO;
import com.plazoleta.dto.request.PropietarioRequestDTO;
import com.plazoleta.dto.response.UsuarioResponseDTO;
import com.plazoleta.entity.EmpleadoRestaurante;
import com.plazoleta.entity.Restaurante;
import com.plazoleta.entity.Rol;
import com.plazoleta.entity.Usuario;
import com.plazoleta.exception.AccesoDenegadoException;
import com.plazoleta.exception.ReglaNegocioException;
import com.plazoleta.repository.EmpleadoRestauranteRepository;
import com.plazoleta.repository.RestauranteRepository;
import com.plazoleta.repository.RolRepository;
import com.plazoleta.repository.UsuarioRepository;
import com.plazoleta.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final UsuarioRepository usuarioRepository;                        // Para guardar y buscar usuarios
    private final RolRepository rolRepository;                                // Para buscar los roles
    private final RestauranteRepository restauranteRepository;                // HU-06: restaurante del empleado
    private final EmpleadoRestauranteRepository empleadoRestauranteRepository; // HU-06: une empleado y restaurante
    private final PasswordEncoder passwordEncoder;                            // Bcrypt (viene de SecurityConfig)

    /**
     * HU-01: crear propietario.
     * Lanza ReglaNegocioException si no se cumple alguna regla;
     * el GlobalExceptionHandler la convierte en una respuesta 400.
     */
    @Override
    public UsuarioResponseDTO crearPropietario(PropietarioRequestDTO dto) {
        // 1. Correo y documento no se pueden repetir (son UNIQUE en la tabla)
        validarQueNoExista(dto.getCorreo(), dto.getDocumentoDeIdentidad());

        // 2. Debe ser mayor de edad.
        //    Period.between calcula los años entre la fecha de nacimiento y hoy.
        int edad = Period.between(dto.getFechaDeNacimiento(), LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new ReglaNegocioException("El propietario debe ser mayor de edad");
        }

        // 3. Queda con el rol PROPIETARIO (se busca en la tabla rol)
        Rol rolPropietario = buscarRol("PROPIETARIO");

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
        return convertir(guardado);
    }

    /**
     * HU-06: crear cuenta de empleado.
     *
     * Que quien llama tenga rol PROPIETARIO ya lo reviso SecurityConfig.
     * Aqui se revisa que sea el dueño del restaurante ("empleados de SU empresa").
     *
     * @Transactional: se guardan 2 cosas (el usuario y su restaurante).
     * Si algo falla en la mitad, se deshace todo y no queda un empleado sin restaurante.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO crearEmpleado(EmpleadoRequestDTO dto, String correoPropietario) {
        // 1. El restaurante debe existir
        Restaurante restaurante = restauranteRepository.findById(dto.getIdRestaurante())
                .orElseThrow(() -> new ReglaNegocioException("El restaurante no existe"));

        // 2. "Solo el propietario puede crearle cuentas a los empleados de su empresa":
        //    el que hizo login debe ser el dueño de ese restaurante. Si no -> 403.
        if (!restaurante.getPropietario().getCorreo().equals(correoPropietario)) {
            throw new AccesoDenegadoException("Solo el propietario del restaurante puede crear sus empleados");
        }

        // 3. Correo y documento no se pueden repetir
        validarQueNoExista(dto.getCorreo(), dto.getDocumentoDeIdentidad());

        // 4. "El usuario quedara con el rol de empleado":
        //    el idRol que llega debe ser el del rol EMPLEADO
        Rol rolEmpleado = buscarRol("EMPLEADO");
        if (!rolEmpleado.getId().equals(dto.getIdRol())) {
            throw new ReglaNegocioException("El idRol debe ser el del rol EMPLEADO (" + rolEmpleado.getId() + ")");
        }

        // 5. Se guarda el usuario con la clave encriptada y rol EMPLEADO
        Usuario empleado = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .documentoDeIdentidad(dto.getDocumentoDeIdentidad())
                .celular(dto.getCelular())
                .correo(dto.getCorreo())
                .clave(passwordEncoder.encode(dto.getClave()))
                .rol(rolEmpleado)
                .build();

        Usuario guardado = usuarioRepository.save(empleado);

        // 6. Se guarda a que restaurante pertenece (tabla empleado_restaurante)
        empleadoRestauranteRepository.save(EmpleadoRestaurante.builder()
                .empleado(guardado)
                .restaurante(restaurante)
                .build());

        return convertir(guardado);
    }

    /**
     * HU-08: el cliente crea su propia cuenta.
     * El formato ya lo reviso el DTO. Aqui solo falta revisar
     * que no exista y guardarlo con el rol CLIENTE.
     */
    @Override
    public UsuarioResponseDTO crearCliente(ClienteRequestDTO dto) {
        // 1. Correo y documento no se pueden repetir
        validarQueNoExista(dto.getCorreo(), dto.getDocumentoDeIdentidad());

        // 2. "El usuario quedara registrado con el rol de cliente"
        Rol rolCliente = buscarRol("CLIENTE");

        // 3. Se guarda con la clave encriptada
        Usuario cliente = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .documentoDeIdentidad(dto.getDocumentoDeIdentidad())
                .celular(dto.getCelular())
                .correo(dto.getCorreo())
                .clave(passwordEncoder.encode(dto.getClave()))
                .rol(rolCliente)
                .build();

        return convertir(usuarioRepository.save(cliente));
    }

    // Correo y documento son UNIQUE en la tabla usuario: si ya existen -> 400
    private void validarQueNoExista(String correo, String documento) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException("El correo ya esta registrado");
        }
        if (usuarioRepository.existsByDocumentoDeIdentidad(documento)) {
            throw new ReglaNegocioException("El documento ya esta registrado");
        }
    }

    // Busca un rol por su nombre en la tabla rol (ej: "PROPIETARIO", "EMPLEADO")
    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new ReglaNegocioException("No existe el rol " + nombre));
    }

    // Convierte la entity en el DTO de respuesta (nunca se devuelve la clave)
    private UsuarioResponseDTO convertir(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .documentoDeIdentidad(usuario.getDocumentoDeIdentidad())
                .celular(usuario.getCelular())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().getNombre())
                .build();
    }
}
