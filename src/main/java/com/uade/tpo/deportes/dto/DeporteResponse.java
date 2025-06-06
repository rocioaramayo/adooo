package com.uade.tpo.deportes.dto;

import com.uade.tpo.deportes.enums.TipoDeporte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeporteResponse {
    private Long id;
    private TipoDeporte tipo;
    private String nombre;
    private Integer jugadoresPorEquipo;
    private String reglasBasicas;
}