package com.uade.tpo.deportes.enums;

public enum EventoPartido {
    PARTIDO_CREADO("Partido creado"),
    JUGADOR_UNIDO("Jugador se unió"),
    PARTIDO_ARMADO("Partido armado"),
    PARTIDO_CONFIRMADO("Partido confirmado"),
    PARTIDO_INICIADO("Partido iniciado"),
    PARTIDO_FINALIZADO("Partido finalizado"),
    PARTIDO_CANCELADO("Partido cancelado");
    
    private final String descripcion;
    
    EventoPartido(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}