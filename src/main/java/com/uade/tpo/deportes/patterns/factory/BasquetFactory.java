package com.uade.tpo.deportes.patterns.factory;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.enums.TipoDeporte;
import org.springframework.stereotype.Component;

@Component
public class BasquetFactory extends DeporteFactory {

    @Override
    public Deporte crearDeporte(TipoDeporte tipo) {
        return Deporte.builder()
                .tipo(TipoDeporte.BASQUET)
                .nombre("Básquet")
                .jugadoresPorEquipo(5)
                .build();
    }

    @Override
    public void configurarReglas(Deporte deporte) {
        String reglas = """
                REGLAS BÁSICAS DEL BÁSQUET:
                1. Dos equipos de 5 jugadores cada uno
                2. Duración: 4 cuartos de 12 minutos
                3. Objetivo: encestar en la canasta contraria
                4. Puntuación: 1, 2 o 3 puntos según la zona de tiro
                5. Faltas personales: máximo 6 por jugador
                6. Regla de 24 segundos para atacar
                7. No se puede regresar el balón al campo propio
                """;
        deporte.setReglasBasicas(reglas);
    }
}