package com.plazoleta.exception;

// Error cuando no se cumple una regla de la HU
// (ej: correo repetido, menor de edad)
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
