package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasUsuarioResponse {
    private Long usuarioId;
    private String nombreUsuario;
    private Integer partidosJugados;
    private Integer partidosOrganizados;
    private Integer partidosFinalizados;
    private Integer partidosCancelados;
    private String deporteFavorito;
}