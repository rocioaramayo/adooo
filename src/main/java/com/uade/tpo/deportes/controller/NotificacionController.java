package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.MessageResponse;
import com.uade.tpo.deportes.patterns.adapter.NotificadorEmail;
import com.uade.tpo.deportes.patterns.adapter.NotificadorPush;
import com.uade.tpo.deportes.patterns.adapter.AdapterFirebasePush;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    @Autowired
    private NotificadorEmail notificadorEmail;
    
    @Autowired
    private NotificadorPush notificadorPush;
    
    @Autowired
    private AdapterFirebasePush adapterFirebasePush;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getFirebaseStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("firebaseConfigured", adapterFirebasePush.isFirebaseConfigured());
        status.put("configurationStatus", adapterFirebasePush.getConfigurationStatus());
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

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
            // Verificar estado de Firebase antes de enviar
            if (!adapterFirebasePush.isFirebaseConfigured()) {
                return ResponseEntity.badRequest().body(
                    MessageResponse.error("Firebase no configurado", 
                        "Firebase no está configurado correctamente. Estado: " + 
                        adapterFirebasePush.getConfigurationStatus())
                );
            }
            
            notificadorPush.enviarNotificacionPush(token, "Notificación de prueba desde UnoMas");
            return ResponseEntity.ok(MessageResponse.success("Push notification enviada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                MessageResponse.error("Error enviando push", e.getMessage())
            );
        }
    }
    
    @PostMapping("/test-push-detailed")
    public ResponseEntity<Map<String, Object>> testearPushDetallado(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar estado de Firebase
            boolean firebaseConfigured = adapterFirebasePush.isFirebaseConfigured();
            String configStatus = adapterFirebasePush.getConfigurationStatus();
            
            response.put("firebaseConfigured", firebaseConfigured);
            response.put("configurationStatus", configStatus);
            
            if (!firebaseConfigured) {
                response.put("success", false);
                response.put("message", "Firebase no configurado correctamente");
                response.put("error", configStatus);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Enviar notificación
            notificadorPush.enviarNotificacionPush(token, "Notificación de prueba detallada desde UnoMas");
            
            response.put("success", true);
            response.put("message", "Push notification enviada exitosamente");
            response.put("token", token.substring(0, Math.min(20, token.length())) + "...");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error enviando push notification");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}