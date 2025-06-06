package com.uade.tpo.deportes.patterns.state;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class NecesitamosJugadoresState implements EstadoPartido {

    @Override
    public void manejarSolicitudUnion(Partido partido, Usuario usuario) {
        if (partido.puedeUnirse(usuario)) {
            partido.getJugadores().add(usuario);
            verificarTransicion(partido);
        } else {
            throw new IllegalArgumentException("El usuario no puede unirse al partido");
        }
    }

    @Override
    public void verificarTransicion(Partido partido) {
        if (partido.getJugadores().size() >= partido.getCantidadJugadoresRequeridos()) {
            partido.cambiarEstado("PARTIDO_ARMADO");
        }
    }

    @Override
    public EstadoPartido obtenerEstadoSiguiente() {
        return new PartidoArmadoState();
    }

    @Override
    public String getNombre() {
        return "NECESITAMOS_JUGADORES";
    }
}