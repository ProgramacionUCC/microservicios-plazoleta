package com.plazoleta.service.impl;

import com.plazoleta.dto.request.LoginRequestDTO;
import com.plazoleta.dto.response.LoginResponseDTO;
import com.plazoleta.entity.Usuario;
import com.plazoleta.exception.CredencialesInvalidasException;
import com.plazoleta.repository.UsuarioRepository;
import com.plazoleta.security.JwtService;
import com.plazoleta.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Login (HU-05).
 *
 * Criterios de la HU:
 *  - El inicio de sesion es con correo y clave.
 *  - Se valida que el usuario exista y que la clave sea correcta.
 *  - Los intentos son ilimitados: si falla solo se informa, no se bloquea.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;  // Bcrypt
    private final JwtService jwtService;            // Crea el token

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // 1. El usuario debe existir (se busca por correo)
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

        // 2. La clave debe coincidir.
        //    En la BD esta encriptada ($2a$10$...), por eso no se compara con equals.
        //    matches() encripta la clave escrita de la misma forma y compara.
        if (!passwordEncoder.matches(dto.getClave(), usuario.getClave())) {
            throw new CredencialesInvalidasException("Clave incorrecta");
        }

        // 3. Todo bien: se entrega un token con el correo y el rol del usuario.
        //    Con ese token el usuario demuestra quien es en las siguientes peticiones.
        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().getNombre());
        return LoginResponseDTO.builder().token(token).build();
    }
}
