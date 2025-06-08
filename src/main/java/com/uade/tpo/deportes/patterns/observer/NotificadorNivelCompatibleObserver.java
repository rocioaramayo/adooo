package com.uade.tpo.deportes.patterns.observer;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.enums.EventoPartido;
import org.springframework.stereotype.Component;

/**
 * Observer que notifica a usuarios con nivel compatible cuando se crea un partido.
 * Complementa el NotificadorDeporteFavoritoObserver para mayor personalización.
 */
@Component
public class NotificadorNivelCompatibleObserver implements ObserverPartido {

    @Override
    public void actualizar(Partido partido, EventoPartido evento) {
        if (evento == EventoPartido.PARTIDO_CREADO) {
            notificarUsuariosNivelCompatible(partido);
        }
    }

    private void notificarUsuariosNivelCompatible(Partido partido) {
        // TODO: Implementar notificaciones por nivel compatible
        System.out.println("🎯 NotificadorNivelCompatible: Procesando partido " + 
                         partido.getId() + " para usuarios compatibles");
    }
}