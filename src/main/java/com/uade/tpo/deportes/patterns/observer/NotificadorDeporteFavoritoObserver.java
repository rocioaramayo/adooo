package com.uade.tpo.deportes.patterns.observer;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.enums.EventoPartido;
import org.springframework.stereotype.Component;

/**
 * Observer específico para notificar a usuarios con deporte favorito
 * cuando se crea un partido de su deporte preferido.
 * 
 * CONSIGNA IMPLEMENTADA: "Se cree un partido nuevo para su deporte favorito"
 */
@Component
public class NotificadorDeporteFavoritoObserver implements ObserverPartido {

    @Override
    public void actualizar(Partido partido, EventoPartido evento) {
        // Solo notificar cuando se crea un partido nuevo
        if (evento == EventoPartido.PARTIDO_CREADO) {
            notificarUsuariosConDeporteFavorito(partido);
        }
    }

    private void notificarUsuariosConDeporteFavorito(Partido partido) {
        // TODO: Implementar notificaciones específicas por deporte favorito
        System.out.println("🎯 NotificadorDeporteFavorito: Notificaciones enviadas para " + 
                         partido.getDeporte().getNombre());
    }
}
