package com.uade.tpo.deportes.patterns.strategy;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;

public interface EstrategiaEmparejamiento {
    boolean puedeUnirse(Usuario usuario, Partido partido);
    Double calcularCompatibilidad(Usuario usuario, Partido partido);
    String getNombre();
}