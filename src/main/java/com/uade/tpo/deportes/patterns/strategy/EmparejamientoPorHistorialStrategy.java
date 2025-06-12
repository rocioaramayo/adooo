package com.uade.tpo.deportes.patterns.strategy;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 📊 ESTRATEGIA POR HISTORIAL - ALGORITMO INTELIGENTE SIMPLE
 * 
 * ALGORITMO:
 * - Sin historial = 60% compatibilidad (neutral)
 * - Historial positivo = hasta 95% compatibilidad
 * - Jugadores conocidos = +20% bonus
 * - Deportes similares jugados = +10% bonus
 * - Usuario activo = +15% bonus
 */
@Component
public class EmparejamientoPorHistorialStrategy implements EstrategiaEmparejamiento {

    @Autowired
    private PartidoRepository partidoRepository;

    @Override
    public boolean puedeUnirse(Usuario usuario, Partido partido) {
        // Verificaciones básicas
        if (partido.getParticipantes().size() >= partido.getCantidadJugadoresRequeridos()) {
            return false;
        }
        
        if (partido.getParticipantes().contains(usuario)) {
            return false;
        }

        // ✨ VERIFICACIÓN INTELIGENTE POR HISTORIAL
        return tieneHistorialCompatible(usuario, partido);
    }

    @Override
    public Double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (!puedeUnirse(usuario, partido)) {
            return 0.0;
        }

        // 🧮 CÁLCULO INTELIGENTE BASADO EN HISTORIAL
        double compatibilidadBase = calcularCompatibilidadBase(usuario);
        double bonusJugadoresConocidos = calcularBonusJugadoresConocidos(usuario, partido);
        double bonusDeportesSimilares = calcularBonusDeportesSimilares(usuario, partido);
        double bonusActividad = calcularBonusActividad(usuario);
        
        double compatibilidadFinal = compatibilidadBase + bonusJugadoresConocidos + 
                                   bonusDeportesSimilares + bonusActividad;
        
        // Mantener en rango [0, 1]
        compatibilidadFinal = Math.max(0.0, Math.min(1.0, compatibilidadFinal));
        
        System.out.println("📊 Compatibilidad historial " + usuario.getNombreUsuario() + " → " +
                         String.format("%.1f%% (Base: %.1f%%, Conocidos: +%.1f%%, Deportes: +%.1f%%, Actividad: +%.1f%%)",
                         compatibilidadFinal * 100, compatibilidadBase * 100, 
                         bonusJugadoresConocidos * 100, bonusDeportesSimilares * 100, bonusActividad * 100));
        
        return compatibilidadFinal;
    }

    // 📊 COMPATIBILIDAD BASE POR EXPERIENCIA
    private double calcularCompatibilidadBase(Usuario usuario) {
        try {
            // Contar partidos del usuario (organizados + jugados)
            List<Partido> partidosOrganizados = partidoRepository.findByOrganizador(usuario);
            List<Partido> partidosJugados = partidoRepository.findPartidosConJugador(usuario);
            
            int totalPartidos = partidosOrganizados.size() + partidosJugados.size();
            
            // Algoritmo de experiencia
            if (totalPartidos == 0) {
                return 0.6; // Nuevo usuario - neutral
            } else if (totalPartidos <= 2) {
                return 0.65; // Principiante
            } else if (totalPartidos <= 5) {
                return 0.75; // Con algo de experiencia
            } else if (totalPartidos <= 10) {
                return 0.85; // Experimentado
            } else {
                return 0.9; // Veterano
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Error calculando historial base: " + e.getMessage());
            return 0.6; // Valor por defecto
        }
    }

    // 🤝 BONUS POR JUGADORES CONOCIDOS
    private double calcularBonusJugadoresConocidos(Usuario usuario, Partido partido) {
        try {
            // Obtener jugadores con los que ya jugó
            Set<Long> jugadoresConocidos = obtenerJugadoresConocidos(usuario);
            
            // Contar cuántos jugadores del partido ya conoce
            long jugadoresConocidosEnPartido = partido.getParticipantes().stream()
                    .mapToLong(j -> jugadoresConocidos.contains(j.getId()) ? 1 : 0)
                    .sum();
            
            // También verificar organizador
            if (jugadoresConocidos.contains(partido.getOrganizador().getId())) {
                jugadoresConocidosEnPartido++;
            }
            
            // Bonus proporcional
            if (jugadoresConocidosEnPartido == 0) {
                return 0.0;
            } else if (jugadoresConocidosEnPartido == 1) {
                return 0.1; // 10% bonus por 1 conocido
            } else if (jugadoresConocidosEnPartido == 2) {
                return 0.15; // 15% bonus por 2 conocidos
            } else {
                return 0.2; // 20% bonus por 3+ conocidos
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Error calculando jugadores conocidos: " + e.getMessage());
            return 0.0;
        }
    }

    // 🏀 BONUS POR DEPORTES SIMILARES
    private double calcularBonusDeportesSimilares(Usuario usuario, Partido partido) {
        try {
            // Obtener deportes que ya jugó
            Set<String> deportesJugados = obtenerDeportesJugados(usuario);
            
            String deportePartido = partido.getDeporte().getTipo().name();
            
            // Si ya jugó este deporte exacto
            if (deportesJugados.contains(deportePartido)) {
                return 0.15; // 15% bonus por experiencia en el deporte
            }
            
            // Si jugó deportes similares
            boolean jugóDeporteSimilar = false;
            switch (deportePartido) {
                case "FUTBOL":
                    jugóDeporteSimilar = deportesJugados.contains("VOLEY");
                    break;
                case "BASQUET":
                    jugóDeporteSimilar = deportesJugados.contains("VOLEY") || deportesJugados.contains("TENIS");
                    break;
                case "VOLEY":
                    jugóDeporteSimilar = deportesJugados.contains("FUTBOL") || deportesJugados.contains("BASQUET");
                    break;
                case "TENIS":
                    jugóDeporteSimilar = deportesJugados.contains("BASQUET");
                    break;
            }
            
            return jugóDeporteSimilar ? 0.08 : 0.0; // 8% bonus por deporte similar
            
        } catch (Exception e) {
            System.err.println("⚠️ Error calculando deportes similares: " + e.getMessage());
            return 0.0;
        }
    }

    // ⚡ BONUS POR ACTIVIDAD RECIENTE
    private double calcularBonusActividad(Usuario usuario) {
        try {
            LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
            
            // Contar partidos recientes
            List<Partido> partidosRecientes = partidoRepository.findByOrganizador(usuario).stream()
                    .filter(p -> p.getCreatedAt().isAfter(hace30Dias))
                    .collect(Collectors.toList());
            
            List<Partido> jugadosRecientes = partidoRepository.findPartidosConJugador(usuario).stream()
                    .filter(p -> p.getCreatedAt().isAfter(hace30Dias))
                    .collect(Collectors.toList());
            
            int partidosRecientesTotales = partidosRecientes.size() + jugadosRecientes.size();
            
            // Bonus por actividad
            if (partidosRecientesTotales >= 3) {
                return 0.15; // Muy activo
            } else if (partidosRecientesTotales >= 1) {
                return 0.08; // Moderadamente activo
            } else {
                return 0.0; // Sin actividad reciente
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Error calculando actividad: " + e.getMessage());
            return 0.0;
        }
    }

    // 🔍 VERIFICAR HISTORIAL COMPATIBLE
    private boolean tieneHistorialCompatible(Usuario usuario, Partido partido) {
        try {
            // Por ahora, siempre permitir (en implementación real verificarías problemas)
            // Aquí podrías verificar:
            // - No hay reportes negativos con otros jugadores
            // - No hay cancelaciones frecuentes
            // - No hay comportamiento problemático
            
            return true;
            
        } catch (Exception e) {
            System.err.println("⚠️ Error verificando historial: " + e.getMessage());
            return true; // Por defecto permitir
        }
    }

    // 🤝 OBTENER JUGADORES CONOCIDOS
    private Set<Long> obtenerJugadoresConocidos(Usuario usuario) {
        try {
            Set<Long> jugadoresConocidos = partidoRepository.findPartidosConJugador(usuario).stream()
                    .flatMap(p -> p.getParticipantes().stream())
                    .filter(j -> !j.getId().equals(usuario.getId()))
                    .map(Usuario::getId)
                    .collect(Collectors.toSet());
            
            // También agregar organizadores de partidos donde participó
            partidoRepository.findPartidosConJugador(usuario).stream()
                    .map(p -> p.getOrganizador().getId())
                    .filter(id -> !id.equals(usuario.getId()))
                    .forEach(jugadoresConocidos::add);
            
            return jugadoresConocidos;
            
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo jugadores conocidos: " + e.getMessage());
            return Set.of();
        }
    }

    // 🏀 OBTENER DEPORTES JUGADOS
    private Set<String> obtenerDeportesJugados(Usuario usuario) {
        try {
            Set<String> deportes = partidoRepository.findPartidosConJugador(usuario).stream()
                    .map(p -> p.getDeporte().getTipo().name())
                    .collect(Collectors.toSet());
            
            // También deportes organizados
            partidoRepository.findByOrganizador(usuario).stream()
                    .map(p -> p.getDeporte().getTipo().name())
                    .forEach(deportes::add);
            
            return deportes;
            
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo deportes jugados: " + e.getMessage());
            return Set.of();
        }
    }

    @Override
    public String getNombre() {
        return "POR_HISTORIAL";
    }
}