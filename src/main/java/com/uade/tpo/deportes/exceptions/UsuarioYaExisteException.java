package com.uade.tpo.deportes.exceptions;

public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException() {
        super("Ya existe un usuario con esos datos");
    }
    
    public UsuarioYaExisteException(String mensaje) {
        super(mensaje);
    }
}