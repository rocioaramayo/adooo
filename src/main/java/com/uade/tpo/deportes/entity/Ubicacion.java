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

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    private String zona;

    public Double calcularDistancia(Ubicacion otra) {
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
}