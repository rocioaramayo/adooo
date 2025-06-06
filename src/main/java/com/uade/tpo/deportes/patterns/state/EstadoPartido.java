package com.uade.tpo.deportes.patterns.state;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;

public interface EstadoPartido {
    void manejarSolicitudUnion(Partido partido, Usuario usuario);
    void verificarTransicion(Partido partido);
    EstadoPartido obtenerEstadoSiguiente();
    String getNombre();
}
