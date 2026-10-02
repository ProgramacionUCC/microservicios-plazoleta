package com.plazoleta.exception;

// Error de login: el correo no existe o la clave no coincide (responde 401)
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
