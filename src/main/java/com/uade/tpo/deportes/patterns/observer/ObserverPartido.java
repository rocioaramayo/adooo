package com.uade.tpo.deportes.patterns.observer;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.enums.EventoPartido;

public interface ObserverPartido {
    void actualizar(Partido partido, EventoPartido evento);
}