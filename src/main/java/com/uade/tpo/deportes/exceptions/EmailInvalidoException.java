package com.uade.tpo.deportes.exceptions;

public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException() {
        super("El formato del email es inválido");
    }
    
    public EmailInvalidoException(String mensaje) {
        super(mensaje);
    }
}