package com.uade.tpo.deportes.patterns.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Adapter para Firebase Push Notifications
 */
@Component
public class AdapterFirebasePush implements NotificadorPush {
    
    @Value("${unomas.notifications.push.enabled:false}")
    private boolean pushEnabled;
    
    @Value("${unomas.notifications.push.firebase.service-account-path}")
    private String serviceAccountPath;
    
    @Value("${unomas.notifications.push.firebase.project-id}")
    private String projectId;
    
    private boolean firebaseEnabled = false;
    private boolean firebaseInitialized = false;

    @PostConstruct
    public void initFirebase() {
        if (!pushEnabled) {
            System.out.println("🔔 Notificaciones push deshabilitadas en configuración");
            return;
        }
        
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Usar la ruta del archivo de credenciales desde la configuración
                if (serviceAccountPath == null || serviceAccountPath.isEmpty()) {
                    System.err.println("❌ Ruta del archivo de credenciales de Firebase no configurada");
                    return;
                }
                
                FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId(projectId)
                        .build();
                FirebaseApp.initializeApp(options);
                firebaseEnabled = true;
                firebaseInitialized = true;
                System.out.println("🔥 Firebase inicializado correctamente para notificaciones push");
                System.out.println("   Proyecto: " + projectId);
                System.out.println("   Archivo de credenciales: " + serviceAccountPath);
            } else {
                firebaseEnabled = true;
                firebaseInitialized = true;
                System.out.println("🔥 Firebase ya estaba inicializado");
            }
        } catch (IOException e) {
            System.err.println("❌ Error inicializando Firebase: " + e.getMessage());
            System.err.println("   Verifica que el archivo de credenciales existe en: " + serviceAccountPath);
            firebaseEnabled = false;
        } catch (Exception e) {
            System.err.println("❌ Error inesperado inicializando Firebase: " + e.getMessage());
            firebaseEnabled = false;
        }
    }

    @Override
    public void enviarNotificacionPush(String token, String mensaje) {
        if (!firebaseEnabled || !firebaseInitialized) {
            System.out.println("🔔 PUSH (simulado) enviado a " + token + ": " + mensaje);
            return;
        }
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                        .setTitle("UnoMas")
                        .setBody(mensaje)
                        .build())
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ PUSH REAL enviado a " + token + ": " + response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando push a " + token + ": " + e.getMessage());
        }
    }

    /**
     * Envío de push con datos personalizados
     */
    public void enviarNotificacionPushPersonalizada(String token, String titulo, String mensaje, 
                                                   java.util.Map<String, String> data) {
        if (!firebaseEnabled || !firebaseInitialized) {
            System.out.println("🔔 PUSH personalizado (simulado): " + titulo + " - " + mensaje);
            if (data != null && !data.isEmpty()) {
                System.out.println("   Datos adicionales: " + data);
            }
            return;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                        .setTitle(titulo)
                        .setBody(mensaje)
                        .build());
            
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            System.out.println("✅ PUSH personalizado REAL enviado: " + titulo + " - " + response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando push personalizado: " + e.getMessage());
        }
    }

    /**
     * Envío masivo a múltiples tokens
     */
    public void enviarNotificacionMasiva(java.util.List<String> tokens, String titulo, String mensaje) {
        if (!firebaseEnabled || !firebaseInitialized) {
            System.out.println("🔔 PUSH masivo (simulado) a " + tokens.size() + " dispositivos: " + titulo);
            tokens.forEach(token -> 
                System.out.println("   → " + token + ": " + mensaje)
            );
            return;
        }

        try {
            // Firebase permite hasta 500 tokens por batch
            int batchSize = 500;
            for (int i = 0; i < tokens.size(); i += batchSize) {
                int end = Math.min(i + batchSize, tokens.size());
                java.util.List<String> batch = tokens.subList(i, end);
                
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build())
                        .build();
                
                // Enviar a cada token del batch
                for (String token : batch) {
                    try {
                        Message tokenMessage = Message.builder()
                                .setToken(token)
                                .setNotification(Notification.builder()
                                    .setTitle(titulo)
                                    .setBody(mensaje)
                                    .build())
                                .build();
                        FirebaseMessaging.getInstance().send(tokenMessage);
                    } catch (Exception e) {
                        System.err.println("❌ Error enviando push a token " + token + ": " + e.getMessage());
                    }
                }
            }
            System.out.println("✅ PUSH masivo REAL enviado a " + tokens.size() + " dispositivos");
        } catch (Exception e) {
            System.err.println("❌ Error enviando push masivo: " + e.getMessage());
        }
    }

    /**
     * Suscribir usuarios a tópicos
     */
    public void suscribirATopico(java.util.List<String> tokens, String topico) {
        if (!firebaseEnabled || !firebaseInitialized) {
            System.out.println("🔔 Suscripción (simulada) de " + tokens.size() + " usuarios al tópico: " + topico);
            return;
        }

        try {
            // Firebase permite hasta 1000 tokens por suscripción
            int batchSize = 1000;
            for (int i = 0; i < tokens.size(); i += batchSize) {
                int end = Math.min(i + batchSize, tokens.size());
                java.util.List<String> batch = tokens.subList(i, end);
                FirebaseMessaging.getInstance().subscribeToTopic(batch, topico);
            }
            System.out.println("✅ " + tokens.size() + " usuarios suscritos al tópico: " + topico);
        } catch (Exception e) {
            System.err.println("❌ Error suscribiendo usuarios al tópico: " + e.getMessage());
        }
    }

    /**
     * Enviar a tópico
     */
    public void enviarNotificacionATopic(String topico, String titulo, String mensaje) {
        if (!firebaseEnabled || !firebaseInitialized) {
            System.out.println("🔔 PUSH a tópico (simulado) " + topico + ": " + titulo);
            return;
        }

        try {
            Message message = Message.builder()
                    .setTopic(topico)
                    .setNotification(Notification.builder()
                        .setTitle(titulo)
                        .setBody(mensaje)
                        .build())
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ PUSH a tópico REAL enviado: " + topico + " - " + response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando push a tópico: " + e.getMessage());
        }
    }

    /**
     * Verificar si Firebase está configurado correctamente
     */
    public boolean isFirebaseConfigured() {
        return firebaseEnabled && firebaseInitialized;
    }

    /**
     * Obtener información de configuración
     */
    public String getConfigurationStatus() {
        if (!pushEnabled) {
            return "Notificaciones push deshabilitadas en configuración";
        }
        if (!firebaseEnabled || !firebaseInitialized) {
            return "Firebase no configurado correctamente - Funcionando en modo simulado";
        }
        return "Firebase configurado y funcionando - Proyecto: " + projectId;
    }

    /**
     * Habilitar Firebase (para cuando esté configurado)
     */
    public void enableFirebase(boolean enabled) {
        this.firebaseEnabled = enabled;
        System.out.println("🔥 Firebase " + (enabled ? "habilitado" : "deshabilitado"));
    }
}