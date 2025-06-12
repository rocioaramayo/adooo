package com.uade.tpo.deportes.patterns.observer;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.enums.EventoPartido;
import com.uade.tpo.deportes.patterns.adapter.NotificadorEmail;
import com.uade.tpo.deportes.patterns.adapter.NotificadorPush;
import com.uade.tpo.deportes.repository.UsuarioRepository;
import com.uade.tpo.deportes.service.pushtoken.PushTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Observer completo para notificaciones - VERSIÓN SIMPLIFICADA
 * Sin dependencias complejas por ahora
 */
@Component
public class NotificadorCompletoObserver implements ObserverPartido {

    @Autowired
    private NotificadorEmail notificadorEmail;
    
    @Autowired
    private NotificadorPush notificadorPush;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PushTokenService pushTokenService;

    @Override
    public void actualizar(Partido partido, EventoPartido evento) {
        try {
            System.out.println("🔔 Procesando notificación: " + evento + " para partido " + partido.getId());
            
            switch (evento) {
                case PARTIDO_CREADO:
                    manejarPartidoCreado(partido);
                    break;
                case PARTIDO_ARMADO:
                    manejarPartidoArmado(partido);
                    break;
                case PARTIDO_CONFIRMADO:
                    manejarPartidoConfirmado(partido);
                    break;
                case PARTIDO_INICIADO:
                    manejarPartidoIniciado(partido);
                    break;
                case PARTIDO_FINALIZADO:
                    manejarPartidoFinalizado(partido);
                    break;
                case PARTIDO_CANCELADO:
                    manejarPartidoCancelado(partido);
                    break;
                default:
                    System.out.println("🔔 Evento no manejado: " + evento);
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando notificación para evento " + evento + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ CASO 1: Notificar cuando se crea un partido nuevo para deporte favorito
    private void manejarPartidoCreado(Partido partido) {
        System.out.println("🆕 Procesando notificaciones para partido creado: " + partido.getId());
        
        try {
            // Obtener usuarios con este deporte como favorito (excepto el organizador)
            List<Usuario> usuariosInteresados = usuarioRepository
                .findByDeporteFavoritoAndActivoTrue(partido.getDeporte().getTipo())
                .stream()
                .filter(usuario -> !usuario.getId().equals(partido.getOrganizador().getId()))
                .filter(Usuario::isActivo)
                .collect(Collectors.toList());

            String mensaje = construirMensajePartidoCreado(partido);
            String asunto = "🆕 Nuevo partido de " + partido.getDeporte().getNombre();

            // Enviar notificaciones
            enviarNotificacionesAUsuarios(usuariosInteresados, asunto, mensaje);

            System.out.println("✅ Notificaciones enviadas a " + usuariosInteresados.size() + 
                              " usuarios interesados en " + partido.getDeporte().getNombre());
        } catch (Exception e) {
            System.err.println("❌ Error en manejarPartidoCreado: " + e.getMessage());
        }
    }

    // ✅ CASO 2: Notificar cuando el partido pasa a "Partido armado"
    private void manejarPartidoArmado(Partido partido) {
        System.out.println("✅ Procesando notificaciones para partido armado: " + partido.getId());
        
        try {
            String mensaje = construirMensajePartidoArmado(partido);
            String asunto = "✅ Partido completo - " + partido.getDeporte().getNombre();

            // Notificar a todos los jugadores y organizador
            List<Usuario> participantes = obtenerTodosLosParticipantes(partido);
            enviarNotificacionesAUsuarios(participantes, asunto, mensaje);

            System.out.println("✅ Notificaciones de partido armado enviadas a " + participantes.size() + " participantes");
        } catch (Exception e) {
            System.err.println("❌ Error en manejarPartidoArmado: " + e.getMessage());
        }
    }

    // ✅ CASO 3: Notificar cuando se confirma el partido
    private void manejarPartidoConfirmado(Partido partido) {
        System.out.println("🎯 Procesando notificaciones para partido confirmado: " + partido.getId());
        
        try {
            String mensaje = construirMensajePartidoConfirmado(partido);
            String asunto = "🎯 Partido confirmado - " + partido.getDeporte().getNombre();

            List<Usuario> participantes = obtenerTodosLosParticipantes(partido);
            enviarNotificacionesAUsuarios(participantes, asunto, mensaje);

            System.out.println("✅ Notificaciones de confirmación enviadas a " + participantes.size() + " participantes");
        } catch (Exception e) {
            System.err.println("❌ Error en manejarPartidoConfirmado: " + e.getMessage());
        }
    }

    // ✅ CASO 4: Notificar cuando el partido pasa a "En juego"
    private void manejarPartidoIniciado(Partido partido) {
        System.out.println("🏃‍♂️ Procesando notificaciones para partido iniciado: " + partido.getId());
        
        try {
            String mensaje = construirMensajePartidoIniciado(partido);
            String asunto = "🏃‍♂️ ¡Tu partido ha comenzado!";

            List<Usuario> participantes = obtenerTodosLosParticipantes(partido);
            enviarNotificacionesAUsuarios(participantes, asunto, mensaje);

            System.out.println("✅ Notificaciones de inicio enviadas a " + participantes.size() + " participantes");
        } catch (Exception e) {
            System.err.println("❌ Error en manejarPartidoIniciado: " + e.getMessage());
        }
    }

    // ✅ CASO 5: Notificar cuando el partido se finaliza
    private void manejarPartidoFinalizado(Partido partido) {
        System.out.println("🏆 Procesando notificaciones para partido finalizado: " + partido.getId());
        
        try {
            String mensaje = construirMensajePartidoFinalizado(partido);
            String asunto = "🏆 Partido finalizado - ¡Gracias por participar!";

            List<Usuario> participantes = obtenerTodosLosParticipantes(partido);
            enviarNotificacionesAUsuarios(participantes, asunto, mensaje);

            System.out.println("✅ Notificaciones de finalización enviadas a " + participantes.size() + " participantes");
        } catch (Exception e) {
            System.err.println("❌ Error en manejarPartidoFinalizado: " + e.getMessage());
        }
    }

    // ✅ CASO 6: Notificar cuando el partido se cancela
    private void manejarPartidoCancelado(Partido partido) {
        System.out.println("❌ Procesando notificaciones para partido cancelado: " + partido.getId());
        
        try {
            String mensaje = construirMensajePartidoCancelado(partido);
            String asunto = "❌ Partido cancelado - " + partido.getDeporte().getNombre();

            List<Usuario> participantes = obtenerTodosLosParticipantes(partido);
            enviarNotificacionesAUsuarios(participantes, asunto, mensaje);

            System.out.println("✅ Notificaciones de cancelación enviadas a " + participantes.size() + " participantes");
        } catch (Exception e) {
            System.err.println("❌ Error en manejarPartidoCancelado: " + e.getMessage());
        }
    }

    // ===== MÉTODOS AUXILIARES =====

    private void enviarNotificacionesAUsuarios(List<Usuario> usuarios, String asunto, String mensaje) {
        if (usuarios.isEmpty()) {
            System.out.println("ℹ️ No hay usuarios para notificar");
            return;
        }

        for (Usuario usuario : usuarios) {
            try {
                // Enviar email
                notificadorEmail.enviarNotificacion(usuario.getEmail(), mensaje);
                
                // Enviar push notification
                List<String> tokens = pushTokenService.obtenerTokensUsuario(usuario.getId());
                if (!tokens.isEmpty()) {
                    for (String token : tokens) {
                        notificadorPush.enviarNotificacionPush(token, mensaje);
                    }
                } else {
                    System.out.println("⚠️ Usuario " + usuario.getNombreUsuario() + " no tiene tokens push registrados");
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error enviando notificación a " + usuario.getEmail() + ": " + e.getMessage());
            }
        }
    }

    private List<Usuario> obtenerTodosLosParticipantes(Partido partido) {
        List<Usuario> participantes = new ArrayList<>();
        
        // Agregar jugadores activos
        if (partido.getParticipantes() != null) {
            participantes.addAll(partido.getParticipantes().stream()
                .filter(Usuario::isActivo)
                .collect(Collectors.toList()));
        }
        
        // Agregar organizador si no está en la lista
        if (partido.getOrganizador() != null && !participantes.contains(partido.getOrganizador())) {
            participantes.add(partido.getOrganizador());
        }
        
        return participantes;
    }

    // Métodos para construir mensajes específicos...

    private String construirMensajePartidoCreado(Partido partido) {
        return String.format(
            "¡Se ha creado un nuevo partido de %s!\n\n" +
            "📍 Ubicación: %s\n" +
            "🕐 Horario: %s\n" +
            "👥 Jugadores necesarios: %d\n" +
            "🎯 Organizador: %s\n\n" +
            "¡Únete ahora desde la app!",
            partido.getDeporte().getNombre(),
            partido.getUbicacion().getDireccion(),
            partido.getHorario().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            partido.getCantidadJugadoresRequeridos(),
            partido.getOrganizador().getNombreUsuario()
        );
    }

    private String construirMensajePartidoArmado(Partido partido) {
        return String.format(
            "¡Excelente! El partido de %s ya tiene suficientes jugadores.\n\n" +
            "📍 Ubicación: %s\n" +
            "🕐 Horario: %s\n" +
            "👥 Jugadores confirmados: %d/%d\n\n" +
            "Esperando confirmación final del organizador.",
            partido.getDeporte().getNombre(),
            partido.getUbicacion().getDireccion(),
            partido.getHorario().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            partido.getCantidadJugadoresRequeridos(),
            partido.getCantidadJugadoresRequeridos()
        );
    }

    private String construirMensajePartidoConfirmado(Partido partido) {
        return String.format(
            "🎯 ¡Tu partido de %s está oficialmente confirmado!\n\n" +
            "📍 Ubicación: %s\n" +
            "🕐 Horario: %s\n" +
            "👥 Jugadores: %d\n\n" +
            "¡Nos vemos en la cancha!",
            partido.getDeporte().getNombre(),
            partido.getUbicacion().getDireccion(),
            partido.getHorario().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            partido.getCantidadJugadoresRequeridos()
        );
    }

    private String construirMensajePartidoIniciado(Partido partido) {
        return String.format(
            "🏃‍♂️ ¡Tu partido de %s ha comenzado!\n\n" +
            "📍 Ubicación: %s\n" +
            "👥 Jugadores: %d\n\n" +
            "¡Que tengas un excelente partido!",
            partido.getDeporte().getNombre(),
            partido.getUbicacion().getDireccion(),
            partido.getCantidadJugadoresRequeridos()
        );
    }

    private String construirMensajePartidoFinalizado(Partido partido) {
        return String.format(
            "🏆 El partido de %s ha finalizado.\n\n" +
            "¡Esperamos que hayas tenido una gran experiencia!\n\n" +
            "📱 No olvides calificar tu experiencia en la app.\n" +
            "🔄 ¡Busca tu próximo partido en UnoMas!",
            partido.getDeporte().getNombre()
        );
    }

    private String construirMensajePartidoCancelado(Partido partido) {
        return String.format(
            "❌ Lamentamos informarte que el partido de %s ha sido cancelado.\n\n" +
            "📍 Ubicación: %s\n" +
            "🕐 Horario original: %s\n\n" +
            "🔄 Busca otros partidos disponibles en UnoMas.",
            partido.getDeporte().getNombre(),
            partido.getUbicacion().getDireccion(),
            partido.getHorario().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }
}