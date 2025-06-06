package com.uade.tpo.deportes.exceptions;

public class PartidoNoEncontradoException extends RuntimeException {
    public PartidoNoEncontradoException() {
        super("Partido no encontrado");
    }
    
    public PartidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
