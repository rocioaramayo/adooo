package com.uade.tpo.deportes.patterns.adapter;

public interface NotificadorPush {
    void enviarNotificacionPush(String token, String mensaje);
}