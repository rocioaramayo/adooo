package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciasResponse {
    private Long id;
    private Long usuarioId;
    private List<DeporteResponse> deportesFavoritos;
    private Integer radioBusqueda;
    private Boolean notificacionesEmail;
    private Boolean notificacionesPush;
    private String zonaPreferida;
} 