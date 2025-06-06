package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasPartidoResponse {
    private Long totalPartidos;
    private Long partidosActivos;
    private Long partidosFinalizados;
    private Long partidosCancelados;
    private Map<String, Long> partidosPorDeporte;
    private Map<String, Long> partidosPorEstado;
    private Double promedioJugadoresPorPartido;
}