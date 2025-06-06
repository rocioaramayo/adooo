package com.uade.tpo.deportes.patterns.state;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConfirmadoState implements EstadoPartido {

    @Override
    public void manejarSolicitudUnion(Partido partido, Usuario usuario) {
        throw new IllegalStateException("El partido ya está confirmado");
    }

    @Override
    public void verificarTransicion(Partido partido) {
        // Transición automática cuando llega la hora del partido
        if (LocalDateTime.now().isAfter(partido.getHorario())) {
            partido.cambiarEstado("EN_JUEGO");
        }
    }

    @Override
    public EstadoPartido obtenerEstadoSiguiente() {
        return new EnJuegoState();
    }

    @Override
    public String getNombre() {
        return "CONFIRMADO";
    }
}