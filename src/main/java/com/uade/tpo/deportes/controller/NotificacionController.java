package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.MessageResponse;
import com.uade.tpo.deportes.patterns.adapter.NotificadorEmail;
import com.uade.tpo.deportes.patterns.adapter.NotificadorPush;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    @Autowired
    private NotificadorEmail notificadorEmail;
    
    @Autowired
    private NotificadorPush notificadorPush;

    @PostMapping("/configurar")
    public ResponseEntity<MessageResponse> configurarNotificaciones() {
        // Endpoint para configurar preferencias de notificaciones
        // En una implementación real, guardarías las preferencias del usuario
        
        return ResponseEntity.ok(MessageResponse.success("Notificaciones configuradas correctamente"));
    }

    @PostMapping("/test-email")
    public ResponseEntity<MessageResponse> testearEmail(@RequestParam String email) {
        try {
            notificadorEmail.enviarNotificacion(email, "Mensaje de prueba desde UnoMas");
            return ResponseEntity.ok(MessageResponse.success("Email de prueba enviado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                MessageResponse.error("Error enviando email", e.getMessage())
            );
        }
    }

    @PostMapping("/test-push")
    public ResponseEntity<MessageResponse> testearPush(@RequestParam String token) {
        try {
            notificadorPush.enviarNotificacionPush(token, "Notificación de prueba desde UnoMas");
            return ResponseEntity.ok(MessageResponse.success("Push notification enviada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                MessageResponse.error("Error enviando push", e.getMessage())
            );
        }
    }
}