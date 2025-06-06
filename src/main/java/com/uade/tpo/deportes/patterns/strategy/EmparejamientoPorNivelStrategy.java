package com.uade.tpo.deportes.patterns.strategy;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.enums.NivelJuego;
import org.springframework.stereotype.Component;

@Component
public class EmparejamientoPorNivelStrategy implements EstrategiaEmparejamiento {
    
    private NivelJuego nivelMinimo = NivelJuego.PRINCIPIANTE;
    private NivelJuego nivelMaximo = NivelJuego.AVANZADO;

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

        // Verificar nivel de juego
        NivelJuego nivelUsuario = usuario.getNivelJuego();
        if (nivelUsuario == null) {
            return false; // Si no tiene nivel definido, no puede unirse
        }

        // Verificar que esté dentro del rango permitido
        return estaEnRango(nivelUsuario);
    }

    @Override
    public Double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (!puedeUnirse(usuario, partido)) {
            return 0.0;
        }

        NivelJuego nivelUsuario = usuario.getNivelJuego();
        
        // Calcular compatibilidad basada en el nivel
        switch (nivelUsuario) {
            case PRINCIPIANTE:
                return 0.6;
            case INTERMEDIO:
                return 0.8;
            case AVANZADO:
                return 1.0;
            default:
                return 0.0;
        }
    }

    @Override
    public String getNombre() {
        return "POR_NIVEL";
    }

    private boolean estaEnRango(NivelJuego nivel) {
        int nivelValue = nivel.ordinal();
        int minValue = nivelMinimo.ordinal();
        int maxValue = nivelMaximo.ordinal();
        
        return nivelValue >= minValue && nivelValue <= maxValue;
    }

    // Setters para configurar el rango
    public void setNivelMinimo(NivelJuego nivelMinimo) {
        this.nivelMinimo = nivelMinimo;
    }

    public void setNivelMaximo(NivelJuego nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }
}