package com.uade.tpo.deportes.patterns.adapter;

import org.springframework.stereotype.Component;

@Component
public class AdapterJavaMailEmail implements NotificadorEmail {
    
    private Object javaMailService; // Simula el servicio de JavaMail

    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        try {
            // Convertir mensaje al formato requerido por JavaMail
            Object mensajeConvertido = convertirMensaje(mensaje);
            
            // Simular envío con JavaMail
            System.out.println("📧 EMAIL enviado a " + destinatario + ": " + mensaje);
            
            // En implementación real:
            // javaMailService.send(destinatario, mensajeConvertido);
            
        } catch (Exception e) {
            System.err.println("Error enviando email a " + destinatario + ": " + e.getMessage());
            throw new RuntimeException("Error en servicio de email", e);
        }
    }

    private Object convertirMensaje(String mensaje) {
        // Convertir el mensaje al formato específico de JavaMail
        // En implementación real crearías un MimeMessage
        return new Object() {
            @Override
            public String toString() {
                return "Mensaje JavaMail: " + mensaje;
            }
        };
    }
}
