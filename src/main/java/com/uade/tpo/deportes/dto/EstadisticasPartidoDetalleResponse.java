package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasPartidoDetalleResponse {
    private Long partidoId;
    private Double promedioCalificacion;
    private Long cantidadCalificaciones;
}