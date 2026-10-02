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

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private static final int EDAD_MINIMA = 18;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // HU-01: el formato ya llega validado por el DTO,
    // aqui van las reglas que necesitan la base de datos o la fecha actual
    @Override
    public UsuarioResponseDTO crearPropietario(PropietarioRequestDTO dto) {
        // 1. Correo y documento no se pueden repetir (UNIQUE en la tabla)
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new ReglaNegocioException("El correo ya esta registrado");
        }
        if (usuarioRepository.existsByDocumentoDeIdentidad(dto.getDocumentoDeIdentidad())) {
            throw new ReglaNegocioException("El documento ya esta registrado");
        }

        // 2. Debe ser mayor de edad
        int edad = Period.between(dto.getFechaDeNacimiento(), LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new ReglaNegocioException("El propietario debe ser mayor de edad");
        }

        // 3. Queda con el rol PROPIETARIO
        Rol rolPropietario = rolRepository.findByNombre("PROPIETARIO")
                .orElseThrow(() -> new ReglaNegocioException("No existe el rol PROPIETARIO"));

        // 4. Se arma el usuario con la clave encriptada y se guarda
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

        Usuario guardado = usuarioRepository.save(usuario);

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
