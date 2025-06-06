package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CambiarEstadoPartidoRequest {
    private String nuevoEstado; // "CONFIRMADO", "CANCELADO", etc.
    private String motivo; // Opcional, para cancelaciones
}