package com.uade.tpo.deportes.dto;

import com.uade.tpo.deportes.enums.NivelJuego;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurarEstrategiaRequest {
    private String tipoEstrategia; // "POR_NIVEL", "POR_CERCANIA", "POR_HISTORIAL"
    
    // Para estrategia por nivel
    private NivelJuego nivelMinimo;
    private NivelJuego nivelMaximo;
    
    // Para estrategia por cercanía
    private Double radioMaximo;
}
