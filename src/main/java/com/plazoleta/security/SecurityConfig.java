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

/**
 * El "portero" del proyecto (HU-05).
 * Aqui se decide QUIEN puede usar CADA endpoint segun su rol.
 *
 * @Configuration = clase de configuracion. Los metodos con @Bean crean
 * objetos que Spring guarda y entrega a quien los necesite.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Herramienta para encriptar claves con bcrypt.
     * La usan UsuarioServiceImpl (encode al crear) y AuthServiceImpl (matches al hacer login).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Reglas de seguridad. Spring las revisa en orden, de arriba hacia abajo:
     * la primera regla que coincida con la peticion es la que se aplica.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF es una proteccion para formularios web con sesion.
                // Como usamos token (no sesion), se desactiva.
                .csrf(csrf -> csrf.disable())

                // STATELESS = el servidor no guarda sesiones.
                // Cada peticion debe traer su token para demostrar quien es.
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Quien puede usar cada endpoint (criterios de la HU-05)
                .authorizeHttpRequests(auth -> auth
                        // El login es libre (si no, nadie podria entrar)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // HU-08: el cliente crea su propia cuenta, todavia no puede tener token
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/cliente").permitAll()
                        // "Creacion de propietario (solo administrador)"
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/propietario").hasRole("ADMINISTRADOR")
                        // "Creacion de empleado (solo propietario)" (HU-05 y HU-06)
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/empleado").hasRole("PROPIETARIO")
                        // "Creacion de restaurante (solo administrador)"
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurantes").hasRole("ADMINISTRADOR")
                        // "Creacion/modificacion de plato (solo propietario del restaurante)"
                        // Aqui se revisa el ROL; que sea EL DUEÑO lo revisa PlatoServiceImpl.
                        .requestMatchers(HttpMethod.POST, "/api/v1/platos").hasRole("PROPIETARIO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/platos/**").hasRole("PROPIETARIO")
                        // "Exponer mis servicios solo a usuarios logueados": todo lo demas pide token
                        .anyRequest().authenticated()
                )

                // Que responder cuando no se cumple una regla
                .exceptionHandling(error -> error
                        // Sin token o token invalido -> 401
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        // Logueado pero con un rol que no tiene permiso -> 403
                        .accessDeniedHandler((request, response, ex) ->
                                response.setStatus(HttpStatus.FORBIDDEN.value())))

                // Nuestro filtro JWT se ejecuta antes que el filtro de login normal de Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
