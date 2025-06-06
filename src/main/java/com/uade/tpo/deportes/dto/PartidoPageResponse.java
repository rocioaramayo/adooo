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
public class PartidoPageResponse {
    private List<PartidoResponse> partidos;
    private long totalPartidos;
    private int paginaActual;
    private int tamañoPagina;
    private boolean hayMasPaginas;
}