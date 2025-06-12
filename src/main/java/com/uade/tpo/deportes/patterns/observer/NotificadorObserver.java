package com.uade.tpo.deportes.patterns.observer;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.enums.EventoPartido;
import com.uade.tpo.deportes.patterns.adapter.NotificadorEmail;
import com.uade.tpo.deportes.patterns.adapter.NotificadorPush;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificadorObserver implements ObserverPartido {

    @Autowired
    private NotificadorEmail notificadorEmail;
    
    @Autowired
    private NotificadorPush notificadorPush;

    @Override
    public void actualizar(Partido partido, EventoPartido evento) {
        String tipoNotificacion = determinarTipoNotificacion(evento);
        String mensaje = construirMensaje(partido, evento);
        
        // Notificar a todos los jugadores del partido
        partido.getParticipantes().forEach(jugador -> {
            try {
                // Enviar email
                notificadorEmail.enviarNotificacion(jugador.getEmail(), mensaje);
                
                // Enviar push (asumiendo que tenemos el token, en implementación real vendría de la base de datos)
                String tokenPush = "token_" + jugador.getId(); // Simulado
                notificadorPush.enviarNotificacionPush(tokenPush, mensaje);
                
            } catch (Exception e) {
                System.err.println("Error enviando notificación a " + jugador.getEmail() + ": " + e.getMessage());
            }
        });

        // También notificar al organizador si no está en la lista de jugadores
        if (!partido.getParticipantes().contains(partido.getOrganizador())) {
            try {
                notificadorEmail.enviarNotificacion(partido.getOrganizador().getEmail(), mensaje);
                String tokenPush = "token_" + partido.getOrganizador().getId();
                notificadorPush.enviarNotificacionPush(tokenPush, mensaje);
            } catch (Exception e) {
                System.err.println("Error enviando notificación al organizador: " + e.getMessage());
            }
        }
    }

    private String determinarTipoNotificacion(EventoPartido evento) {
        switch (evento) {
            case PARTIDO_CREADO:
                return "PARTIDO_NUEVO";
            case JUGADOR_UNIDO:
                return "JUGADOR_NUEVO";
            case PARTIDO_ARMADO:
                return "PARTIDO_COMPLETO";
            case PARTIDO_CONFIRMADO:
                return "PARTIDO_CONFIRMADO";
            case PARTIDO_INICIADO:
                return "PARTIDO_INICIADO";
            case PARTIDO_FINALIZADO:
                return "PARTIDO_FINALIZADO";
            case PARTIDO_CANCELADO:
                return "PARTIDO_CANCELADO";
            default:
                return "ACTUALIZACION_PARTIDO";
        }
    }

    private String construirMensaje(Partido partido, EventoPartido evento) {
        String deporte = partido.getDeporte().getNombre();
        String ubicacion = partido.getUbicacion().getDireccion();
        
        switch (evento) {
            case PARTIDO_CREADO:
                return String.format("¡Nuevo partido de %s creado en %s! Únete ahora.", deporte, ubicacion);
            case JUGADOR_UNIDO:
                return String.format("Se unió un nuevo jugador al partido de %s en %s.", deporte, ubicacion);
            case PARTIDO_ARMADO:
                return String.format("¡Partido de %s completo! Esperando confirmación.", deporte);
            case PARTIDO_CONFIRMADO:
                return String.format("Partido de %s confirmado para %s en %s.", deporte, 
                    partido.getHorario().toString(), ubicacion);
            case PARTIDO_INICIADO:
                return String.format("¡El partido de %s ha comenzado!", deporte);
            case PARTIDO_FINALIZADO:
                return String.format("El partido de %s ha finalizado. ¡Gracias por participar!", deporte);
            case PARTIDO_CANCELADO:
                return String.format("El partido de %s en %s ha sido cancelado.", deporte, ubicacion);
            default:
                return String.format("Actualización en el partido de %s.", deporte);
        }
    }
}