package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartidoResponse {
    private Long id;
    private DeporteResponse deporte;
    private Integer cantidadJugadoresRequeridos;
    private Integer cantidadJugadoresActual;
    private Integer duracion;
    private UbicacionResponse ubicacion;
    private LocalDateTime horario;
    private UsuarioResponse organizador;
    private List<UsuarioResponse> jugadores;
    private String estado;
    private String estrategiaEmparejamiento;
    private LocalDateTime createdAt;
    private boolean puedeUnirse;
    private Double compatibilidad;
}