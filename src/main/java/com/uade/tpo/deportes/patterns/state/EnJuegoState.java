package com.uade.tpo.deportes.patterns.state;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EnJuegoState implements EstadoPartido {

    @Override
    public void manejarSolicitudUnion(Partido partido, Usuario usuario) {
        throw new IllegalStateException("El partido ya está en curso");
    }

    @Override
    public void verificarTransicion(Partido partido) {
        // Transición automática cuando termina el tiempo del partido
        LocalDateTime finEstimado = partido.getHorario().plusMinutes(partido.getDuracion());
        if (LocalDateTime.now().isAfter(finEstimado)) {
            partido.cambiarEstado("FINALIZADO");
        }
    }

    @Override
    public EstadoPartido obtenerEstadoSiguiente() {
        return new FinalizadoState();
    }

    @Override
    public String getNombre() {
        return "EN_JUEGO";
    }
}