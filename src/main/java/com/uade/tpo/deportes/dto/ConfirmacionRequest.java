package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmacionRequest {
    private Long partidoId;
    private boolean confirmar; // true = confirmar, false = rechazar
    private String motivo; // Opcional, para rechazos
}
