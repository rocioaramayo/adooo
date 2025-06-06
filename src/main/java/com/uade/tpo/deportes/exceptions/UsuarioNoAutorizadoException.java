package com.uade.tpo.deportes.exceptions;

public class UsuarioNoAutorizadoException extends RuntimeException {
    public UsuarioNoAutorizadoException() {
        super("Usuario no autorizado para esta operación");
    }
    
    public UsuarioNoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}