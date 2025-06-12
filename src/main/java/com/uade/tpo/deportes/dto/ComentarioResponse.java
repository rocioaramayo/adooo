package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioResponse {
    private Long id;
    private Long partidoId;
    private Long usuarioId;
    private String nombreUsuario;
    private String contenido;
    private Integer calificacion;
    private LocalDateTime fechaCreacion;
}