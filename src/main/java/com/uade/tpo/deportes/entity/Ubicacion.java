package com.uade.tpo.deportes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ubicaciones")
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String direccion;

    // ✅ CAMBIO: Permitir null en coordenadas
    @Column(nullable = true)  // ✅ CAMBIADO: Ahora permite null
    private Double latitud;

    @Column(nullable = true)  // ✅ CAMBIADO: Ahora permite null
    private Double longitud;

    private String zona; // Ya permite null

    public Double calcularDistancia(Ubicacion otra) {
        // ✅ MEJORADO: Verificar null antes de calcular
        if (this.latitud == null || this.longitud == null || 
            otra.latitud == null || otra.longitud == null) {
            return null; // No se puede calcular distancia sin coordenadas
        }
        
        // Fórmula de Haversine simplificada para calcular distancia
        final int R = 6371; // Radio de la Tierra en km
        
        double latDistance = Math.toRadians(otra.latitud - this.latitud);
        double lonDistance = Math.toRadians(otra.longitud - this.longitud);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitud)) * Math.cos(Math.toRadians(otra.latitud))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distancia en km
    }

    // ✅ NUEVO: Método para verificar si tiene coordenadas
    public boolean tieneCoordenadasCompletas() {
        return latitud != null && longitud != null;
    }

    // ✅ NUEVO: Método para obtener coordenadas como string
    public String obtenerCoordenadasString() {
        if (tieneCoordenadasCompletas()) {
            return String.format("%.6f, %.6f", latitud, longitud);
        }
        return "Coordenadas no disponibles";
    }
}