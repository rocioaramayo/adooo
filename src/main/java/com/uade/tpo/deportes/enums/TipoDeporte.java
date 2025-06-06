package com.uade.tpo.deportes.enums;

public enum TipoDeporte {
    FUTBOL("Fútbol", 11),
    BASQUET("Básquet", 5),
    VOLEY("Vóley", 6),
    TENIS("Tenis", 2);
    
    private final String nombre;
    private final int jugadoresPorEquipo;
    
    TipoDeporte(String nombre, int jugadoresPorEquipo) {
        this.nombre = nombre;
        this.jugadoresPorEquipo = jugadoresPorEquipo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getJugadoresPorEquipo() {
        return jugadoresPorEquipo;
    }
}