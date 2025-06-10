package com.uade.tpo.deportes.service.scheduler;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.patterns.observer.NotificadorCompletoObserver;
import com.uade.tpo.deportes.patterns.observer.NotificadorObserver;
import com.uade.tpo.deportes.repository.PartidoRepository;
import com.uade.tpo.deportes.service.partido.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    @Autowired
    private PartidoService partidoService;
        @Autowired
    private NotificadorCompletoObserver notificadorCompletoObserver;
     @Autowired
    private NotificadorObserver notificadorObserver;
    @Autowired
    private PartidoRepository partidoRepository;

    // ⏰ TRANSICIÓN 1: CONFIRMADO → EN_JUEGO (cada minuto)
@Scheduled(fixedRate = 60000)
@Transactional
public void iniciarPartidosConfirmados() {
    LocalDateTime ahora = LocalDateTime.now();
    LocalDateTime hace5Minutos = ahora.minusMinutes(5);
    
    System.out.println("🔄 [" + ahora + "] Verificando partidos para iniciar...");
    
    List<Partido> partidosParaIniciar = partidoRepository.findPartidosParaIniciar(ahora, hace5Minutos);
    
    for (Partido partido : partidosParaIniciar) {
        try {
            System.out.println("🏃‍♂️ Iniciando partido ID:" + partido.getId());
            
            // ✅ RECONECTAR OBSERVERS ANTES DE NOTIFICAR
            reconectarObserversEnScheduled(partido);
            
            // TRANSICIÓN STATE: CONFIRMADO → EN_JUEGO
            partido.cambiarEstado("EN_JUEGO");
            partidoRepository.save(partido);
            
            // ✅ TRIGGER OBSERVER: Notificaciones automáticas
            System.out.println("🔔 Disparando notificaciones automáticas para partido " + partido.getId());
            partido.notificarObservers();
            
            System.out.println("✅ Partido iniciado exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error iniciando partido " + partido.getId() + ": " + e.getMessage());
        }
    }
}
private void reconectarObserversEnScheduled(Partido partido) {
    // Solo obtener los observers que necesitamos (sin inyección compleja)
    if (partido.getObservers() == null) {
        partido.setObservers(new ArrayList<>());
    }
    
    // Al menos agregar el observer básico
    partido.agregarObserver(notificadorObserver);
    
    // Si están disponibles, agregar los otros
    try {
        partido.agregarObserver(notificadorCompletoObserver);
    } catch (Exception e) {
        System.out.println("⚠️ NotificadorCompletoObserver no disponible en scheduled task");
    }
}

    // ⏰ TRANSICIÓN 2: EN_JUEGO → FINALIZADO (cada 5 minutos)
    @Scheduled(fixedRate = 300000) // 5 minutos
    @Transactional
    public void finalizarPartidosEnJuego() {
        LocalDateTime ahora = LocalDateTime.now();
        
        System.out.println("🔄 [" + ahora + "] Verificando partidos para finalizar...");
        
        // Buscar partidos EN_JUEGO que deberían haber terminado
        List<Partido> partidosEnJuego = partidoRepository.findByEstadoActual("EN_JUEGO");
        
        int partidosFinalizados = 0;
        
        for (Partido partido : partidosEnJuego) {
            try {
                // Calcular hora de finalización (hora inicio + duración)
                LocalDateTime horaFinalizacion = partido.getHorario().plusMinutes(partido.getDuracion());
                
                // Si ya pasó la hora de finalización + margen de 15 minutos
                if (ahora.isAfter(horaFinalizacion.plusMinutes(15))) {
                    
                    System.out.println("🏆 Finalizando partido ID:" + partido.getId() + 
                                     " - Duración: " + partido.getDuracion() + " min" +
                                     " - Inicio: " + partido.getHorario() +
                                     " - Fin calculado: " + horaFinalizacion);
                    
                    // TRANSICIÓN STATE: EN_JUEGO → FINALIZADO
                    partido.cambiarEstado("FINALIZADO");
                    partidoRepository.save(partido);
                    
                    // TRIGGER OBSERVER: Notificaciones de finalización
                    partido.notificarObservers();
                    
                    partidosFinalizados++;
                    System.out.println("✅ Partido finalizado automáticamente");
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error finalizando partido " + partido.getId() + ": " + e.getMessage());
            }
        }
        
        if (partidosFinalizados > 0) {
            System.out.println("🎯 " + partidosFinalizados + " partidos finalizados automáticamente");
        }
    }

    // ⏰ LIMPIEZA: Cancelar partidos abandonados (cada 30 minutos)
    @Scheduled(fixedRate = 1800000) // 30 minutos
    @Transactional
    public void cancelarPartidosAbandonados() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hace2Horas = ahora.minusHours(2);
        
        System.out.println("🧹 [" + ahora + "] Limpiando partidos abandonados...");
        
        // Buscar partidos en NECESITAMOS_JUGADORES que están muy atrasados
        List<Partido> partidosAbandonados = partidoRepository.findByHorarioBetween(hace2Horas, ahora.minusMinutes(30))
            .stream()
            .filter(p -> "NECESITAMOS_JUGADORES".equals(p.getEstadoActual()))
            .toList();
        
        int partidosCancelados = 0;
        
        for (Partido partido : partidosAbandonados) {
            try {
                System.out.println("❌ Cancelando partido abandonado ID:" + partido.getId() + 
                                 " - Creado: " + partido.getCreatedAt() +
                                 " - Horario: " + partido.getHorario());
                
                // TRANSICIÓN STATE: CUALQUIER_ESTADO → CANCELADO
                partido.cambiarEstado("CANCELADO");
                partidoRepository.save(partido);
                
                // TRIGGER OBSERVER: Notificaciones de cancelación
                partido.notificarObservers();
                
                partidosCancelados++;
                
            } catch (Exception e) {
                System.err.println("❌ Error cancelando partido abandonado " + partido.getId() + ": " + e.getMessage());
            }
        }
        
        if (partidosCancelados > 0) {
            System.out.println("🗑️ " + partidosCancelados + " partidos abandonados cancelados");
        }
    }

    // ⏰ RECORDATORIOS: Notificar 1 hora antes (cada 15 minutos)
    @Scheduled(fixedRate = 900000) // 15 minutos
    @Transactional
    public void enviarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime en1Hora = ahora.plusHours(1);
        LocalDateTime en45Minutos = ahora.plusMinutes(45);
        
        // Buscar partidos CONFIRMADOS que inician en 45-60 minutos
        List<Partido> partidosProximos = partidoRepository.findByHorarioBetween(en45Minutos, en1Hora)
            .stream()
            .filter(p -> "CONFIRMADO".equals(p.getEstadoActual()))
            .toList();
        
        if (!partidosProximos.isEmpty()) {
            System.out.println("⏰ Enviando recordatorios para " + partidosProximos.size() + " partidos próximos");
            
            for (Partido partido : partidosProximos) {
                try {
                    // TRIGGER OBSERVER: Recordatorio especial
                    partido.notificarObservers();
                    System.out.println("📱 Recordatorio enviado para partido " + partido.getId());
                } catch (Exception e) {
                    System.err.println("❌ Error enviando recordatorio: " + e.getMessage());
                }
            }
        }
    }

    // 📊 ESTADÍSTICAS: Reporte automático cada hora
    @Scheduled(fixedRate = 3600000) // 1 hora
    public void generarReporteAutomatico() {
        try {
            LocalDateTime ahora = LocalDateTime.now();
            
            long partidosActivos = partidoRepository.countByEstado("NECESITAMOS_JUGADORES") + 
                                  partidoRepository.countByEstado("PARTIDO_ARMADO") +
                                  partidoRepository.countByEstado("CONFIRMADO");
            
            long partidosEnJuego = partidoRepository.countByEstado("EN_JUEGO");
            long partidosFinalizadosHoy = partidoRepository.findByHorarioBetween(
                ahora.toLocalDate().atStartOfDay(), ahora)
                .stream()
                .mapToLong(p -> "FINALIZADO".equals(p.getEstadoActual()) ? 1 : 0)
                .sum();
            
            System.out.println("\n📊 === REPORTE AUTOMÁTICO [" + ahora + "] ===");
            System.out.println("🎯 Partidos activos: " + partidosActivos);
            System.out.println("🏃‍♂️ Partidos en juego: " + partidosEnJuego);
            System.out.println("🏆 Partidos finalizados hoy: " + partidosFinalizadosHoy);
            System.out.println("===============================================\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error generando reporte automático: " + e.getMessage());
        }
    }
}