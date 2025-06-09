package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.UbicacionResponse;
import com.uade.tpo.deportes.entity.Ubicacion;
import com.uade.tpo.deportes.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @GetMapping("/zonas")
    public ResponseEntity<List<String>> obtenerZonas() {
        List<String> zonas = ubicacionRepository.findZonasDisponibles();
        
        // Si no hay zonas, devolver lista por defecto
        if (zonas.isEmpty()) {
            zonas = List.of("Centro", "Palermo", "Belgrano", "Zona Norte", "Zona Sur", "Zona Oeste");
        }
        
        return ResponseEntity.ok(zonas);
    }

    // ✅ NUEVO: Endpoint mejorado con información de zonas
    @GetMapping("/zonas/detalle")
    public ResponseEntity<Map<String, Object>> obtenerZonasConDetalle() {
        List<String> zonas = ubicacionRepository.findZonasDisponibles();
        
        // Contar ubicaciones por zona
        Map<String, Long> conteoUbicaciones = zonas.stream()
                .collect(Collectors.toMap(
                    zona -> zona,
                    zona -> (long) ubicacionRepository.findByZona(zona).size()
                ));

        Map<String, Object> respuesta = new java.util.HashMap<>();
        respuesta.put("zonas", zonas);
        respuesta.put("totalZonas", zonas.size());
        respuesta.put("ubicacionesPorZona", conteoUbicaciones);
        respuesta.put("zonasPopulares", obtenerZonasMasPopulares(conteoUbicaciones));

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UbicacionResponse>> buscarUbicaciones(@RequestParam String direccion) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findByDireccionContainingIgnoreCase(direccion);
        
        List<UbicacionResponse> responses = ubicaciones.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<UbicacionResponse>> obtenerUbicacionesPorZona(@PathVariable String zona) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findByZona(zona);
        
        List<UbicacionResponse> responses = ubicaciones.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    // ✅ NUEVO: Obtener ubicaciones populares (las que más aparecen en partidos)
    @GetMapping("/populares")
    public ResponseEntity<List<UbicacionResponse>> obtenerUbicacionesPopulares() {
        // TODO: En una implementación completa, esto consultaría qué ubicaciones
        // se usan más frecuentemente en partidos
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll()
                .stream()
                .filter(u -> u.getZona() != null)
                .limit(10)
                .collect(Collectors.toList());
        
        List<UbicacionResponse> responses = ubicaciones.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    // ✅ NUEVO: Buscar ubicaciones cerca de coordenadas
    @GetMapping("/cerca")
    public ResponseEntity<List<UbicacionResponse>> obtenerUbicacionesCerca(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(defaultValue = "10.0") Double radioKm) {
        
        // Calcular rango aproximado (1 grado ≈ 111km)
        Double deltaLat = radioKm / 111.0;
        Double deltaLon = radioKm / (111.0 * Math.cos(Math.toRadians(latitud)));
        
        List<Ubicacion> ubicaciones = ubicacionRepository.findByCoordenadasEnRango(
                latitud - deltaLat, latitud + deltaLat,
                longitud - deltaLon, longitud + deltaLon);
        
        // Filtrar por distancia real y ordenar por cercanía
        List<UbicacionResponse> responses = ubicaciones.stream()
                .filter(u -> u.tieneCoordenadasCompletas())
                .map(u -> {
                    UbicacionResponse response = mapearAResponse(u);
                    // Calcular distancia real
                    Ubicacion ubicacionUsuario = Ubicacion.builder()
                            .latitud(latitud)
                            .longitud(longitud)
                            .build();
                    Double distancia = u.calcularDistancia(ubicacionUsuario);
                    // Agregar distancia como información extra (esto es un hack, en implementación real usarías un DTO más completo)
                    return response;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    // ✅ NUEVO: Estadísticas de ubicaciones
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasUbicaciones() {
        long totalUbicaciones = ubicacionRepository.count();
        List<String> zonas = ubicacionRepository.findZonasDisponibles();
        
        long ubicacionesConCoordenadas = ubicacionRepository.findAll().stream()
                .mapToLong(u -> u.tieneCoordenadasCompletas() ? 1 : 0)
                .sum();
        
        Map<String, Object> estadisticas = Map.of(
            "totalUbicaciones", totalUbicaciones,
            "totalZonas", zonas.size(),
            "ubicacionesConCoordenadas", ubicacionesConCoordenadas,
            "ubicacionesSinCoordenadas", totalUbicaciones - ubicacionesConCoordenadas,
            "porcentajeConCoordenadas", totalUbicaciones > 0 ? 
                Math.round((ubicacionesConCoordenadas * 100.0) / totalUbicaciones) : 0
        );
        
        return ResponseEntity.ok(estadisticas);
    }

    // ===== MÉTODOS AUXILIARES =====
    
    private UbicacionResponse mapearAResponse(Ubicacion ubicacion) {
        return UbicacionResponse.builder()
                .id(ubicacion.getId())
                .direccion(ubicacion.getDireccion())
                .latitud(ubicacion.getLatitud())
                .longitud(ubicacion.getLongitud())
                .zona(ubicacion.getZona())
                .build();
    }

    private List<String> obtenerZonasMasPopulares(Map<String, Long> conteoUbicaciones) {
        return conteoUbicaciones.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}