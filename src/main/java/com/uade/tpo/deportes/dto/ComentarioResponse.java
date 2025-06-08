package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioResponse {
    private Long id;
    private String usuarioNombre;
    private String comentario;
    private Integer calificacion;
    private LocalDateTime fechaCreacion;
}