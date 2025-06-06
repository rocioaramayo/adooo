package com.uade.tpo.deportes.exceptions;

public class EstadoPartidoInvalidoException extends RuntimeException {
    public EstadoPartidoInvalidoException() {
        super("Estado del partido inválido para esta operación");
    }
    
    public EstadoPartidoInvalidoException(String mensaje) {
        super(mensaje);
    }
}