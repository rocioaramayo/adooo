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
public class RegisterRequest {
    private String nombreUsuario;
    private String email;
    private String contrasena;
    private TipoDeporte deporteFavorito; // Opcional
    private NivelJuego nivelJuego; // Opcional
}
