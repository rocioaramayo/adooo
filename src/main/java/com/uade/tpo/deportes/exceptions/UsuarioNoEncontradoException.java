package com.uade.tpo.deportes.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException() {
        super("Usuario no encontrado");
    }
    
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}