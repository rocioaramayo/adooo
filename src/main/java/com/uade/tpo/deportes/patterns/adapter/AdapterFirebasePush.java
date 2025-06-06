package com.uade.tpo.deportes.patterns.adapter;

import org.springframework.stereotype.Component;

@Component
public class AdapterFirebasePush implements NotificadorPush {
    
    private Object firebaseService; // Simula el servicio de Firebase

    @Override
    public void enviarNotificacionPush(String token, String mensaje) {
        try {
            // Convertir mensaje al formato requerido por Firebase
            Object mensajeFirebase = convertirAFormatoFirebase(mensaje);
            
            // Simular envío con Firebase
            System.out.println("🔔 PUSH enviado a " + token + ": " + mensaje);
            
            // En implementación real:
            // firebaseService.sendToDevice(token, mensajeFirebase);
            
        } catch (Exception e) {
            System.err.println("Error enviando push a " + token + ": " + e.getMessage());
            throw new RuntimeException("Error en servicio de push", e);
        }
    }

    private Object convertirAFormatoFirebase(String mensaje) {
        // Convertir el mensaje al formato específico de Firebase
        return new Object() {
            private final String title = "UnoMas - Notificación";
            private final String body = mensaje;
            
            @Override
            public String toString() {
                return String.format("Firebase Message: {title: '%s', body: '%s'}", title, body);
            }
        };
    }
}
