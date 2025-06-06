package com.uade.tpo.deportes.exceptions;

public class PartidoCompletoException extends RuntimeException {
    public PartidoCompletoException() {
        super("El partido ya está completo");
    }
    
    public PartidoCompletoException(String mensaje) {
        super(mensaje);
    }
}