package com.plazoleta.service;

import com.plazoleta.dto.request.ClienteRequestDTO;
import com.plazoleta.dto.request.EmpleadoRequestDTO;
import com.plazoleta.dto.request.PropietarioRequestDTO;
import com.plazoleta.dto.response.UsuarioResponseDTO;

/**
 * INTERFAZ del service: dice QUE se puede hacer con usuarios,
 * pero no COMO. El "como" esta en service/impl/UsuarioServiceImpl.
 *
 * Asi lo trabaja la profe: el controller depende de la interfaz,
 * no de la implementacion. Si mañana cambia la forma de hacerlo,
 * el controller no se toca.
 */
public interface UsuarioService {

    // HU-01: recibe los datos ya validados y devuelve el propietario creado
    UsuarioResponseDTO crearPropietario(PropietarioRequestDTO propietarioRequestDTO);

    // HU-06: crea un empleado en un restaurante del propietario que hizo login.
    // correoPropietario sale del token (es quien esta creando al empleado).
    UsuarioResponseDTO crearEmpleado(EmpleadoRequestDTO empleadoRequestDTO, String correoPropietario);

    // HU-08: el cliente crea su propia cuenta (no necesita login)
    UsuarioResponseDTO crearCliente(ClienteRequestDTO clienteRequestDTO);
}
