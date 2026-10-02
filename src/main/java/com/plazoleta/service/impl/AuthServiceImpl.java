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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // HU-05: login con correo y clave. Los intentos son ilimitados
    // (si falla solo se informa el error, no se bloquea nada).
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // 1. El usuario debe existir
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

        // 2. La clave debe coincidir con la guardada en bcrypt
        if (!passwordEncoder.matches(dto.getClave(), usuario.getClave())) {
            throw new CredencialesInvalidasException("Clave incorrecta");
        }

        // 3. Se entrega un token con el correo y el rol del usuario
        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol().getNombre());
        return LoginResponseDTO.builder().token(token).build();
    }
}
