package com.uade.tpo.deportes.patterns.factory;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.enums.TipoDeporte;
import org.springframework.stereotype.Component;

@Component
public class VoleyFactory extends DeporteFactory {

    @Override
    public Deporte crearDeporte(TipoDeporte tipo) {
        return Deporte.builder()
                .tipo(TipoDeporte.VOLEY)
                .nombre("Vóley")
                .jugadoresPorEquipo(6)
                .build();
    }

    @Override
    public void configurarReglas(Deporte deporte) {
        String reglas = """
                REGLAS BÁSICAS DEL VÓLEY:
                1. Dos equipos de 6 jugadores cada uno
                2. Máximo 3 toques por equipo antes de pasar la red
                3. Gana el set quien llega primero a 25 puntos (con 2 de diferencia)
                4. Partida al mejor de 5 sets
                5. No se puede tocar la red
                6. Rotación de jugadores en sentido horario
                7. Saque desde detrás de la línea de fondo
                """;
        deporte.setReglasBasicas(reglas);
    }
}
