package com.plazoleta.controller;

import com.plazoleta.dto.request.ClienteRequestDTO;
import com.plazoleta.dto.request.EmpleadoRequestDTO;
import com.plazoleta.dto.request.PropietarioRequestDTO;
import com.plazoleta.dto.response.UsuarioResponseDTO;
import com.plazoleta.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CONTROLLER = la "puerta de entrada". Recibe las peticiones de Postman
 * y devuelve la respuesta. No tiene reglas de negocio: se las pasa al service.
 */
@RestController                       // Esta clase recibe peticiones y responde JSON
@RequestMapping("/api/v1/usuarios")   // Todas las rutas de esta clase empiezan asi
@RequiredArgsConstructor              // Spring nos inyecta el UsuarioService
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * HU-01: crear propietario.
     * POST http://localhost:8080/api/v1/usuarios/propietario
     * Solo ADMINISTRADOR (esa regla esta en SecurityConfig, HU-05).
     *
     * @RequestBody: convierte el JSON que llega en un PropietarioRequestDTO.
     * @Valid: revisa las anotaciones del DTO (@NotBlank, @Email...) ANTES de entrar.
     *         Si algo falla, ni siquiera se ejecuta este metodo: responde 400.
     */
    @PostMapping("/propietario")
    public ResponseEntity<UsuarioResponseDTO> crearPropietario(@Valid @RequestBody PropietarioRequestDTO propietarioRequestDTO) {
        // ResponseEntity permite elegir el codigo HTTP: 201 CREATED = se creo algo nuevo
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crearPropietario(propietarioRequestDTO));
    }

    /**
     * HU-06: crear cuenta de empleado.
     * POST http://localhost:8080/api/v1/usuarios/empleado
     * Solo PROPIETARIO (regla en SecurityConfig) y solo en SU restaurante (lo revisa el service).
     *
     * authentication.getName() = correo del propietario que hizo login (sale del token).
     */
    @PostMapping("/empleado")
    public ResponseEntity<UsuarioResponseDTO> crearEmpleado(@Valid @RequestBody EmpleadoRequestDTO empleadoRequestDTO,
                                                            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)  // 201
                .body(usuarioService.crearEmpleado(empleadoRequestDTO, authentication.getName()));
    }

    /**
     * HU-08: el cliente crea su propia cuenta.
     * POST http://localhost:8080/api/v1/usuarios/cliente
     * Es libre (sin token): el cliente todavia no tiene cuenta para iniciar sesion.
     */
    @PostMapping("/cliente")
    public ResponseEntity<UsuarioResponseDTO> crearCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)  // 201
                .body(usuarioService.crearCliente(clienteRequestDTO));
    }
}
