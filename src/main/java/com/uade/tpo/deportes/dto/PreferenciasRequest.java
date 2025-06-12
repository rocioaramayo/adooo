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
public class PreferenciasRequest {
    private List<Long> deportesFavoritosIds;
    private Integer radioBusqueda;
    private Boolean notificacionesEmail;
    private Boolean notificacionesPush;
    private String zonaPreferida;
} 