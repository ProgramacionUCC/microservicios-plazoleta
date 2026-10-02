package com.plazoleta.exception;

// El usuario esta logueado pero no tiene permiso para esa accion (responde 403)
// Ej: un propietario intentando modificar un plato de otro restaurante
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
