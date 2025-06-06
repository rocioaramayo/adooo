package com.uade.tpo.deportes.service.estadisticas;

import com.uade.tpo.deportes.dto.EstadisticasGeneralesResponse;
import com.uade.tpo.deportes.repository.PartidoRepository;
import com.uade.tpo.deportes.repository.UbicacionRepository;
import com.uade.tpo.deportes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EstadisticasServiceImpl implements EstadisticasService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Override
    public EstadisticasGeneralesResponse obtenerEstadisticasGenerales() {
        // Estadísticas de usuarios
        long totalUsuarios = usuarioRepository.count();
        long usuariosActivos = usuarioRepository.countUsuariosActivos();
        
        // Estadísticas de partidos
        long totalPartidos = partidoRepository.count();
        long partidosActivos = partidoRepository.countByEstado("NECESITAMOS_JUGADORES") + 
                              partidoRepository.countByEstado("PARTIDO_ARMADO") +
                              partidoRepository.countByEstado("CONFIRMADO") +
                              partidoRepository.countByEstado("EN_JUEGO");
        long partidosFinalizados = partidoRepository.countByEstado("FINALIZADO");
        long partidosCancelados = partidoRepository.countByEstado("CANCELADO");
        
        // Mapas de distribución
        Map<String, Long> usuariosPorDeporte = convertirAMap(usuarioRepository.contarUsuariosPorDeporte());
        Map<String, Long> usuariosPorNivel = convertirAMap(usuarioRepository.contarUsuariosPorNivel());
        Map<String, Long> partidosPorDeporte = convertirAMap(partidoRepository.contarPartidosPorDeporte());
        Map<String, Long> partidosPorEstado = convertirAMap(partidoRepository.contarPartidosPorEstado());
        
        // Métricas calculadas
        Double promedioJugadores = partidoRepository.promedioJugadoresPorPartido();
        String deporteMasPopular = obtenerClaveMasPopular(partidosPorDeporte);
        String zonaMasActiva = obtenerZonaMasActiva();
        
        return EstadisticasGeneralesResponse.builder()
                .totalUsuarios(totalUsuarios)
                .usuariosActivos(usuariosActivos)
                .totalPartidos(totalPartidos)
                .partidosActivos(partidosActivos)
                .partidosFinalizados(partidosFinalizados)
                .partidosCancelados(partidosCancelados)
                .usuariosPorDeporte(usuariosPorDeporte)
                .usuariosPorNivel(usuariosPorNivel)
                .partidosPorDeporte(partidosPorDeporte)
                .partidosPorEstado(partidosPorEstado)
                .promedioJugadoresPorPartido(promedioJugadores != null ? promedioJugadores : 0.0)
                .deporteMasPopular(deporteMasPopular)
                .zonaMasActiva(zonaMasActiva)
                .build();
    }

    private Map<String, Long> convertirAMap(List<Object[]> resultados) {
        Map<String, Long> mapa = new HashMap<>();
        for (Object[] resultado : resultados) {
            String clave = resultado[0] != null ? resultado[0].toString() : "Sin especificar";
            Long valor = resultado[1] != null ? ((Number) resultado[1]).longValue() : 0L;
            mapa.put(clave, valor);
        }
        return mapa;
    }

    private String obtenerClaveMasPopular(Map<String, Long> mapa) {
        return mapa.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No disponible");
    }

    private String obtenerZonaMasActiva() {
        List<String> zonas = ubicacionRepository.findZonasDisponibles();
        // En una implementación real, contarías partidos por zona
        return zonas.isEmpty() ? "No disponible" : zonas.get(0);
    }
}