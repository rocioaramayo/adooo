package com.uade.tpo.deportes.service.scheduler;

import com.uade.tpo.deportes.service.partido.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    @Autowired
    private PartidoService partidoService;

    // Ejecutar cada 5 minutos para verificar transiciones automáticas
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void procesarTransicionesAutomaticas() {
        try {
            partidoService.procesarTransicionesAutomaticas();
        } catch (Exception e) {
            System.err.println("Error procesando transiciones automáticas: " + e.getMessage());
        }
    }
}