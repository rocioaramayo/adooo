package com.uade.tpo.deportes.dto;

import com.uade.tpo.deportes.enums.NivelJuego;
import com.uade.tpo.deportes.enums.TipoDeporte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CriteriosBusqueda {
    private TipoDeporte tipoDeporte;
    private String zona;
    private Double radioKm;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private NivelJuego nivelMinimo;
    private NivelJuego nivelMaximo;
    private boolean soloDisponibles;
    private String ordenarPor; // "fecha", "distancia", "compatibilidad"
    private String orden; // "asc", "desc"
}