package com.uade.tpo.deportes.patterns.state;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class CanceladoState implements EstadoPartido {

    @Override
    public void manejarSolicitudUnion(Partido partido, Usuario usuario) {
        throw new IllegalStateException("El partido ha sido cancelado");
    }

    @Override
    public void verificarTransicion(Partido partido) {
        // Estado final, no hay más transiciones
    }

    @Override
    public EstadoPartido obtenerEstadoSiguiente() {
        return this; // Se mantiene en el mismo estado
    }

    @Override
    public String getNombre() {
        return "CANCELADO";
    }
}
