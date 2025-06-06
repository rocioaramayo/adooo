package com.uade.tpo.deportes.patterns.strategy;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.entity.Ubicacion;
import org.springframework.stereotype.Component;

@Component
public class EmparejamientoPorCercaniaStrategy implements EstrategiaEmparejamiento {
    
    private Double radioMaximo = 10.0; // km por defecto

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

        // Para esta implementación simple, asumimos que todos pueden unirse
        // En una implementación real, necesitarías la ubicación del usuario
        return true;
    }

    @Override
    public Double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (!puedeUnirse(usuario, partido)) {
            return 0.0;
        }

        // Por ahora retornamos compatibilidad alta para todos
        // En implementación real calcularías distancia real
        return 0.9;
    }

    @Override
    public String getNombre() {
        return "POR_CERCANIA";
    }

    public void setRadioMaximo(Double radioMaximo) {
        this.radioMaximo = radioMaximo;
    }
}