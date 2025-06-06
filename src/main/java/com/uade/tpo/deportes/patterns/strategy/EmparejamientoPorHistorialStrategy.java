package com.uade.tpo.deportes.patterns.strategy;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class EmparejamientoPorHistorialStrategy implements EstrategiaEmparejamiento {

    @Override
    public boolean puedeUnirse(Usuario usuario, Partido partido) {
        // Verificar que el partido no esté lleno
        if (partido.getJugadores().size() >= partido.getCantidadJugadoresRequeridos()) {
            return false;
        }

        // Verificar que el usuario no esté ya en el partido
        if (partido.getJugadores().contains(usuario)) {
            return false;
        }

        // Verificar historial previo con otros jugadores
        return verificarHistorialPrevio(usuario, partido);
    }

    @Override
    public Double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (!puedeUnirse(usuario, partido)) {
            return 0.0;
        }

        // Calcular compatibilidad basada en historial
        // Por ahora retornamos un valor fijo
        return 0.75;
    }

    @Override
    public String getNombre() {
        return "POR_HISTORIAL";
    }

    private boolean verificarHistorialPrevio(Usuario usuario, Partido partido) {
        // Implementación simplificada - en un caso real consultarías la base de datos
        // para verificar partidos previos entre jugadores
        return true;
    }
}