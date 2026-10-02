package com.plazoleta.exception;

/**
 * Error de login (HU-05): el correo no existe o la clave no coincide.
 * El GlobalExceptionHandler la convierte en 401 (no autorizado).
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
