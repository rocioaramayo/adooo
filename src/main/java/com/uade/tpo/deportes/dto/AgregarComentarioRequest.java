package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgregarComentarioRequest {
    private Long partidoId;
    private String comentario;
    private Integer calificacion; // 1-5

    // Setter específico para resolver el error
    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }
}
