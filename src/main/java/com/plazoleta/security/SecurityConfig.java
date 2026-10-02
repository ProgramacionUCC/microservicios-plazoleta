package com.plazoleta.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Herramienta para encriptar las claves con bcrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // HU-05: quien puede usar cada endpoint
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // No se guardan sesiones: cada peticion trae su token
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // El login es libre
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Crear propietario y crear restaurante: solo ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/propietario").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurantes").hasRole("ADMINISTRADOR")
                        // Crear y modificar plato: solo PROPIETARIO (que sea el dueño lo revisa el service)
                        .requestMatchers(HttpMethod.POST, "/api/v1/platos").hasRole("PROPIETARIO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/platos/**").hasRole("PROPIETARIO")
                        // Todo lo demas: solo usuarios logueados
                        .anyRequest().authenticated()
                )
                .exceptionHandling(error -> error
                        // Sin token o token invalido -> 401
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        // Logueado pero con un rol que no tiene permiso -> 403
                        .accessDeniedHandler((request, response, ex) ->
                                response.setStatus(HttpStatus.FORBIDDEN.value())))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
