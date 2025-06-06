package com.uade.tpo.deportes.dto;

import com.uade.tpo.deportes.enums.NivelJuego;
import com.uade.tpo.deportes.enums.TipoDeporte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarPerfilRequest {
    private TipoDeporte deporteFavorito;
    private NivelJuego nivelJuego;
}