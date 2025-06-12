package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarComentarioRequest {
    private Long partidoId;
    private String contenido;
    private Integer calificacion; // 1-5 estrellas

    // Setter específico para resolver el error
    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }
}
