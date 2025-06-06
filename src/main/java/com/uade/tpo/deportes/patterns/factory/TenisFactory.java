package com.uade.tpo.deportes.patterns.factory;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.enums.TipoDeporte;
import org.springframework.stereotype.Component;

@Component
public class TenisFactory extends DeporteFactory {

    @Override
    public Deporte crearDeporte(TipoDeporte tipo) {
        return Deporte.builder()
                .tipo(TipoDeporte.TENIS)
                .nombre("Tenis")
                .jugadoresPorEquipo(1) // Individual, 2 para dobles
                .build();
    }

    @Override
    public void configurarReglas(Deporte deporte) {
        String reglas = """
                REGLAS BÁSICAS DEL TENIS:
                1. Individual: 1 vs 1, Dobles: 2 vs 2
                2. Puntuación: 15, 30, 40, juego
                3. Gana el set quien primero llegue a 6 juegos (con 2 de diferencia)
                4. Partida al mejor de 3 o 5 sets
                5. La pelota debe pasar por encima de la red
                6. Solo se permite un rebote en cada lado
                7. Saque alternado cada juego
                """;
        deporte.setReglasBasicas(reglas);
    }
}