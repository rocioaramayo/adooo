package com.uade.tpo.deportes.patterns.strategy;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.enums.NivelJuego;
import org.springframework.stereotype.Component;
/**
 *
 * 
 * ALGORITMO INTELIGENTE:
 * - Mismo nivel = 100% compatibilidad
 * - 1 nivel diferencia = 70% compatibilidad  
 * - 2 niveles diferencia = 30% compatibilidad
 * - Más de 2 niveles = 0% compatibilidad
 */

@Component
public class EmparejamientoPorNivelStrategy implements EstrategiaEmparejamiento {
    
    private NivelJuego nivelMinimo = NivelJuego.PRINCIPIANTE;
    private NivelJuego nivelMaximo = NivelJuego.AVANZADO;

    @Override
    public boolean puedeUnirse(Usuario usuario, Partido partido) {
        // 1. Verificaciones básicas
        if (partido.getJugadores().size() >= partido.getCantidadJugadoresRequeridos()) {
            return false;
        }
        
        if (partido.getJugadores().contains(usuario)) {
            return false;
        }
        
        if (usuario.getNivelJuego() == null) {
            return false;
        }

        // 2. Verificar rango permitido
        if (!estaEnRango(usuario.getNivelJuego())) {
            return false;
        }

        // 3. ✨ LÓGICA INTELIGENTE: Verificar compatibilidad con jugadores existentes
        return esCompatibleConJugadoresExistentes(usuario, partido);
    }

    @Override
    public Double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (!puedeUnirse(usuario, partido)) {
            return 0.0;
        }

        NivelJuego nivelUsuario = usuario.getNivelJuego();
        
        // 🧮 CÁLCULO SOFISTICADO DE COMPATIBILIDAD
        double compatibilidadBase = calcularCompatibilidadBase(nivelUsuario);
        double compatibilidadGrupal = calcularCompatibilidadConGrupo(usuario, partido);
        double bonusOrganizador = calcularBonusOrganizador(usuario, partido);
        
        // Promedio ponderado
        double compatibilidadFinal = (compatibilidadBase * 0.4) + 
                                   (compatibilidadGrupal * 0.5) + 
                                   (bonusOrganizador * 0.1);
        
        System.out.println("🎯 Compatibilidad " + usuario.getNombreUsuario() + " → " +
                         String.format("%.1f%% (Base: %.1f%%, Grupal: %.1f%%, Bonus: %.1f%%)",
                         compatibilidadFinal * 100, compatibilidadBase * 100, 
                         compatibilidadGrupal * 100, bonusOrganizador * 100));
        
        return Math.min(1.0, compatibilidadFinal);
    }

    // 🎯 CÁLCULO BASE POR NIVEL
    private double calcularCompatibilidadBase(NivelJuego nivel) {
        switch (nivel) {
            case PRINCIPIANTE:
                return 0.6; // Buenos para aprender
            case INTERMEDIO:
                return 1.0; // Nivel óptimo, se adaptan a todos
            case AVANZADO:
                return 0.8; // Buenos pero pueden intimidar principiantes
            default:
                return 0.0;
        }
    }

    // 🤝 COMPATIBILIDAD CON GRUPO EXISTENTE
    private double calcularCompatibilidadConGrupo(Usuario usuario, Partido partido) {
        if (partido.getJugadores().isEmpty()) {
            return 1.0; // Primer jugador, compatibilidad máxima
        }

        NivelJuego nivelUsuario = usuario.getNivelJuego();
        
        // Calcular distribución de niveles en el partido
        long principiantes = partido.getJugadores().stream()
                .mapToLong(j -> j.getNivelJuego() == NivelJuego.PRINCIPIANTE ? 1 : 0).sum();
        long intermedios = partido.getJugadores().stream()
                .mapToLong(j -> j.getNivelJuego() == NivelJuego.INTERMEDIO ? 1 : 0).sum();
        long avanzados = partido.getJugadores().stream()
                .mapToLong(j -> j.getNivelJuego() == NivelJuego.AVANZADO ? 1 : 0).sum();
        
        // 🎲 LÓGICA DE BALANCEADO DE GRUPO
        switch (nivelUsuario) {
            case PRINCIPIANTE:
                // Principiantes prefieren grupos con otros principiantes o intermedios
                if (principiantes > 0 || intermedios > 0) return 0.9;
                if (avanzados > 2) return 0.3; // Demasiados avanzados intimidan
                return 0.6;
                
            case INTERMEDIO:
                // Intermedios son el "pegamento" - siempre buena compatibilidad
                return 0.95;
                
            case AVANZADO:
                // Avanzados prefieren desafío pero no quieren ser solo ellos
                if (avanzados > 0) return 1.0; // Hay otros avanzados
                if (intermedios > principiantes) return 0.8; // Más intermedios que principiantes
                if (principiantes > 2) return 0.4; // Demasiados principiantes
                return 0.7;
                
            default:
                return 0.5;
        }
    }

    // 🎖️ BONUS POR COMPATIBILIDAD CON ORGANIZADOR
    private double calcularBonusOrganizador(Usuario usuario, Partido partido) {
        NivelJuego nivelOrganizador = partido.getOrganizador().getNivelJuego();
        NivelJuego nivelUsuario = usuario.getNivelJuego();
        
        if (nivelOrganizador == null) return 0.0;
        
        // Mismo nivel que organizador = bonus
        if (nivelOrganizador == nivelUsuario) {
            return 0.2; // 20% bonus
        }
        
        // Un nivel de diferencia = bonus menor
        int diferencia = Math.abs(nivelOrganizador.ordinal() - nivelUsuario.ordinal());
        if (diferencia == 1) {
            return 0.1; // 10% bonus
        }
        
        return 0.0; // Sin bonus
    }

    // 🔍 VERIFICAR COMPATIBILIDAD CON JUGADORES EXISTENTES
    private boolean esCompatibleConJugadoresExistentes(Usuario usuario, Partido partido) {
        if (partido.getJugadores().isEmpty()) {
            return true; // Primer jugador
        }

        NivelJuego nivelUsuario = usuario.getNivelJuego();
        
        // Verificar que no haya más de 2 niveles de diferencia con cualquier jugador
        for (Usuario jugador : partido.getJugadores()) {
            if (jugador.getNivelJuego() != null) {
                int diferencia = Math.abs(nivelUsuario.ordinal() - jugador.getNivelJuego().ordinal());
                if (diferencia > 2) {
                    return false; // Demasiada diferencia de nivel
                }
            }
        }

        return true;
    }

    private boolean estaEnRango(NivelJuego nivel) {
        int nivelValue = nivel.ordinal();
        int minValue = nivelMinimo.ordinal();
        int maxValue = nivelMaximo.ordinal();
        
        return nivelValue >= minValue && nivelValue <= maxValue;
    }

    // Configuración
    public void setNivelMinimo(NivelJuego nivelMinimo) {
        this.nivelMinimo = nivelMinimo;
        System.out.println("🎯 Estrategia POR_NIVEL configurada - Mínimo: " + nivelMinimo);
    }

    public void setNivelMaximo(NivelJuego nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
        System.out.println("🎯 Estrategia POR_NIVEL configurada - Máximo: " + nivelMaximo);
    }

    @Override
    public String getNombre() {
        return "POR_NIVEL";
    }
}
