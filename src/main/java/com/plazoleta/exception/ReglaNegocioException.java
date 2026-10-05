package com.plazoleta.exception;

/**
 * Error propio para cuando NO se cumple una regla de la HU.
 * Ej: correo repetido, menor de edad, restaurante que no existe.
 *
 * Los services la lanzan con "throw new ReglaNegocioException(mensaje)"
 * y el GlobalExceptionHandler la convierte en una respuesta 400:
 *   { "mensaje": "El correo ya esta registrado" }
 *
 * Extiende RuntimeException para no tener que poner "throws" en cada metodo.
 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
