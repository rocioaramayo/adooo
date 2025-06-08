package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TipoDeporteResponse {
    private String value;  // FUTBOL, BASQUET, etc.
    private String label;  // Fútbol, Básquet, etc.
}

// 2. Actualizar DeporteController.java