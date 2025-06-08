package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasPartidoDetalleResponse {
    private Long partidoId;
    private Integer jugadoresQueAsistieron;
    private Integer duracionRealMinutos;
    private Double calificacionPromedio;
    private Integer totalComentarios;
    private List<ComentarioResponse> comentarios;
    private String mensaje;
}