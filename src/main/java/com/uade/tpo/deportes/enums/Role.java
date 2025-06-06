package com.uade.tpo.deportes.enums;

public enum Role {
    JUGADOR("Jugador"),
    ORGANIZADOR("Organizador"),
    ADMIN("Administrador");
    
    private final String descripcion;
    
    Role(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}