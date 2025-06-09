package com.uade.tpo.deportes.patterns.strategy;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.entity.Ubicacion;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmparejamientoPorCercaniaStrategy implements EstrategiaEmparejamiento {
    
    private Double radioMaximo = 15.0; // km por defecto
    private static final Map<String, List<String>> ZONAS_CERCANAS = initZonasCercanas();

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

        // ✅ NUEVO: Por ahora permitimos que todos se unan
        // En una implementación completa, aquí verificarías:
        // 1. Ubicación del usuario (desde su perfil o una tabla de ubicaciones de usuario)
        // 2. Calcular distancia real
        // 3. Verificar zona compatible
        
        return true;
    }

    @Override
    public Double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (!puedeUnirse(usuario, partido)) {
            return 0.0;
        }

        // ✅ MEJORADO: Calcular compatibilidad basada en zona
        String zonaPartido = partido.getUbicacion().getZona();
        
        // Si no hay zona definida, compatibilidad media
        if (zonaPartido == null || zonaPartido.trim().isEmpty()) {
            return 0.5;
        }

        // ✅ TODO: Aquí deberías obtener la zona preferida del usuario
        // Por ahora usamos una lógica simplificada
        String zonaUsuario = obtenerZonaPreferidaUsuario(usuario);
        
        if (zonaUsuario == null) {
            return 0.6; // Usuario sin zona preferida
        }

        // Misma zona = compatibilidad alta
        if (zonaPartido.equals(zonaUsuario)) {
            return 1.0;
        }

        // Zonas cercanas = compatibilidad media-alta
        if (sonZonasCercanas(zonaPartido, zonaUsuario)) {
            return 0.8;
        }

        // Zonas lejanas = compatibilidad baja pero no imposible
        return 0.3;
    }

    @Override
    public String getNombre() {
        return "POR_CERCANIA";
    }

    // ✅ NUEVO: Configurar radio máximo
    public void setRadioMaximo(Double radioMaximo) {
        this.radioMaximo = radioMaximo;
    }

    public Double getRadioMaximo() {
        return radioMaximo;
    }

    // ✅ NUEVO: Métodos auxiliares para zonas
    private String obtenerZonaPreferidaUsuario(Usuario usuario) {
        // TODO: En una implementación completa, esto vendría de:
        // 1. Una tabla de preferencias de usuario
        // 2. El historial de partidos del usuario
        // 3. Una encuesta de ubicación
        
        // Por ahora, lógica simplificada basada en deporte favorito
        if (usuario.getDeporteFavorito() == null) {
            return null;
        }

        switch (usuario.getDeporteFavorito()) {
            case FUTBOL:
                return "Zona Sur"; // Los futboleros prefieren la zona sur 😄
            case BASQUET:
                return "Palermo"; // Basquet en Palermo
            case TENIS:
                return "Zona Norte"; // Tenis en zona norte
            case VOLEY:
                return "Puerto Madero"; // Voley en Puerto Madero
            default:
                return "Centro"; // Por defecto centro
        }
    }

    private boolean sonZonasCercanas(String zona1, String zona2) {
        if (zona1 == null || zona2 == null) {
            return false;
        }

        List<String> zonasAdyacentes = ZONAS_CERCANAS.get(zona1.toLowerCase());
        if (zonasAdyacentes == null) {
            return false;
        }

        return zonasAdyacentes.contains(zona2.toLowerCase());
    }

    // ✅ NUEVO: Mapa de zonas cercanas
    private static Map<String, List<String>> initZonasCercanas() {
        Map<String, List<String>> mapa = new HashMap<>();
        
        // Definir qué zonas están cerca de cada zona
        mapa.put("centro", Arrays.asList("san telmo", "recoleta", "puerto madero"));
        mapa.put("puerto madero", Arrays.asList("centro", "san telmo", "la boca"));
        mapa.put("palermo", Arrays.asList("belgrano", "villa crespo", "recoleta"));
        mapa.put("belgrano", Arrays.asList("palermo", "zona norte"));
        mapa.put("villa crespo", Arrays.asList("palermo", "caballito"));
        mapa.put("caballito", Arrays.asList("villa crespo", "flores"));
        mapa.put("flores", Arrays.asList("caballito", "zona oeste"));
        mapa.put("la boca", Arrays.asList("puerto madero", "san telmo", "zona sur"));
        mapa.put("san telmo", Arrays.asList("centro", "puerto madero", "la boca"));
        mapa.put("recoleta", Arrays.asList("centro", "palermo"));
        
        // Zonas del GBA
        mapa.put("zona norte", Arrays.asList("belgrano", "zona central"));
        mapa.put("zona oeste", Arrays.asList("flores", "zona central"));
        mapa.put("zona sur", Arrays.asList("la boca", "zona central"));
        mapa.put("zona central", Arrays.asList("zona norte", "zona oeste", "zona sur"));
        
        return mapa;
    }

    // ✅ NUEVO: Método para obtener zonas recomendadas para un usuario
    public List<String> obtenerZonasRecomendadas(Usuario usuario) {
        String zonaPreferida = obtenerZonaPreferidaUsuario(usuario);
        if (zonaPreferida == null) {
            return Arrays.asList("Centro", "Palermo", "Belgrano"); // Zonas populares por defecto
        }

        List<String> zonasAdyacentes = ZONAS_CERCANAS.get(zonaPreferida.toLowerCase());
        if (zonasAdyacentes != null) {
            return zonasAdyacentes;
        }

        return Arrays.asList(zonaPreferida);
    }

    // ✅ NUEVO: Calcular distancia real si ambas ubicaciones tienen coordenadas
    public Double calcularDistanciaReal(Ubicacion ubicacion1, Ubicacion ubicacion2) {
        if (ubicacion1 == null || ubicacion2 == null) {
            return null;
        }

        if (!ubicacion1.tieneCoordenadasCompletas() || !ubicacion2.tieneCoordenadasCompletas()) {
            return null;
        }

        return ubicacion1.calcularDistancia(ubicacion2);
    }

    // ✅ NUEVO: Verificar si está dentro del radio máximo
    public boolean estaEnRadio(Ubicacion ubicacion1, Ubicacion ubicacion2) {
        Double distancia = calcularDistanciaReal(ubicacion1, ubicacion2);
        if (distancia == null) {
            // Si no podemos calcular distancia, usamos zonas
            return sonZonasCercanas(ubicacion1.getZona(), ubicacion2.getZona());
        }
        return distancia <= radioMaximo;
    }
}