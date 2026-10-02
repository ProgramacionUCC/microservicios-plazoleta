package com.plazoleta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Atrapa los errores de TODO el proyecto en un solo lugar y responde
 * un mensaje claro con su codigo HTTP.
 *
 * Sin esta clase, cuando algo falla Spring responde un error generico
 * (500) dificil de entender. Con ella, el que llama ve exactamente que paso.
 *
 * Funciona asi: cuando en cualquier parte se lanza una excepcion,
 * Spring busca aqui un metodo con @ExceptionHandler de ese tipo
 * y usa su respuesta.
 */
@RestControllerAdvice  // Aplica a todos los controllers
public class GlobalExceptionHandler {

    /**
     * Errores de FORMATO del DTO (@NotBlank, @Email, @Pattern...).
     * Spring lanza MethodArgumentNotValidException cuando @Valid encuentra errores.
     * Se arma un mapa campo -> mensaje para mostrar TODOS los errores juntos:
     *   { "correo": "El correo no es valido", "nombre": "El nombre es obligatorio" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);  // 400
    }

    // Reglas de negocio que no se cumplen (lanzadas desde los services) -> 400
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> manejarReglaNegocio(ReglaNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", ex.getMessage()));
    }

    // Login con correo o clave incorrectos (HU-05) -> 401
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> manejarCredenciales(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", ex.getMessage()));
    }

    // Logueado pero sin permiso para esa accion, ej: no es el dueño (HU-05) -> 403
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, String>> manejarAccesoDenegado(AccesoDenegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", ex.getMessage()));
    }
}
