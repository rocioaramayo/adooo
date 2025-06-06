package com.uade.tpo.deportes.patterns.factory;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.enums.TipoDeporte;
import org.springframework.stereotype.Component;

@Component
public class FutbolFactory extends DeporteFactory {

    @Override
    public Deporte crearDeporte(TipoDeporte tipo) {
        return Deporte.builder()
                .tipo(TipoDeporte.FUTBOL)
                .nombre("Fútbol")
                .jugadoresPorEquipo(11)
                .build();
    }

    @Override
    public void configurarReglas(Deporte deporte) {
        String reglas = """
                REGLAS BÁSICAS DEL FÚTBOL:
                1. Dos equipos de 11 jugadores cada uno
                2. Duración: 90 minutos (dos tiempos de 45 minutos)
                3. Objetivo: meter goles en la portería contraria
                4. Solo el portero puede usar las manos dentro del área
                5. Offside: no se puede estar más adelantado que el último defensor
                6. Faltas: tarjetas amarillas y rojas por mal comportamiento
                7. Saques: lateral, de esquina, de meta
                """;
        deporte.setReglasBasicas(reglas);
    }
}
