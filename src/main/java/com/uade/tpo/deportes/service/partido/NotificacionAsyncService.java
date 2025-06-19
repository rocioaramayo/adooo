package com.uade.tpo.deportes.service.partido;

import com.uade.tpo.deportes.entity.Partido;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificacionAsyncService {
    @Async
    public void notificarCreacionPartido(Partido partido) {
        partido.notificarObservers();
    }
} 