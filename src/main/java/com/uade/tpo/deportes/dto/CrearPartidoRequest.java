package com.uade.tpo.deportes.dto;

import com.uade.tpo.deportes.enums.TipoDeporte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrearPartidoRequest {
    private TipoDeporte tipoDeporte;
    private Integer cantidadJugadoresRequeridos;
    private Integer duracion; // en minutos
    private UbicacionRequest ubicacion;
    private LocalDateTime horario;
    private String estrategiaEmparejamiento; // "POR_NIVEL", "POR_CERCANIA", "POR_HISTORIAL"
}
