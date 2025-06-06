package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UbicacionRequest {
    private String direccion;
    private Double latitud;
    private Double longitud;
    private String zona;
}