package com.plazoleta.exception;

/**
 * El usuario SI inicio sesion, pero no tiene permiso para esa accion (HU-05).
 * Ej: un propietario intentando modificar un plato de otro restaurante.
 * El GlobalExceptionHandler la convierte en 403 (prohibido).
 *
 * Diferencia:
 *   401 = no se sabe quien eres (no hiciste login o el token no sirve)
 *   403 = se sabe quien eres, pero no puedes hacer eso
 */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
