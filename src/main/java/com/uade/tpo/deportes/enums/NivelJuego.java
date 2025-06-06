package com.uade.tpo.deportes.enums;

public enum NivelJuego {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado");
    
    private final String descripcion;
    
    NivelJuego(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}