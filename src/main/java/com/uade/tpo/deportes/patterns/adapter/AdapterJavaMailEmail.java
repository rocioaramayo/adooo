package com.uade.tpo.deportes.patterns.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementación REAL de JavaMail - Reemplaza la versión simulada
 */
@Component
public class AdapterJavaMailEmail implements NotificadorEmail {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${unomas.notifications.email.from:UnoMas <noreply@unomas.com>}")
    private String fromEmail;
    
    @Value("${unomas.notifications.email.enabled:true}")
    private boolean emailEnabled;

    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        if (!emailEnabled) {
            System.out.println("📧 Email deshabilitado - Mensaje para " + destinatario + ": " + mensaje);
            return;
        }

        try {
            // ✅ ENVÍO REAL de email
            enviarEmailTexto(destinatario, "Notificación UnoMas", mensaje);
            System.out.println("✅ Email REAL enviado exitosamente a: " + destinatario);
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando email REAL a " + destinatario + ": " + e.getMessage());
            // En caso de error, mostrar pero no fallar la aplicación
            System.out.println("📧 Fallback: EMAIL (simulado) para " + destinatario + ": " + mensaje);
        }
    }

    // ✅ MÉTODO REAL para envío de email simple
    private void enviarEmailTexto(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(crearContenidoEmailCompleto(mensaje));
        
        mailSender.send(mailMessage);
    }

    // ✅ NUEVO: Crear contenido de email más atractivo
    private String crearContenidoEmailCompleto(String mensajePrincipal) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("🏃‍♂️ UnoMas - Sistema de Gestión Deportiva\n");
        contenido.append("=" .repeat(50)).append("\n\n");
        contenido.append(mensajePrincipal);
        contenido.append("\n\n");
        contenido.append("=" .repeat(50)).append("\n");
        contenido.append("📱 Descarga la app: http://localhost:5173\n");
        contenido.append("🌐 Dashboard: http://localhost:5173/dashboard\n");
        contenido.append("📧 Este correo fue enviado automáticamente por UnoMas\n");
        contenido.append("⚠️ No responder a esta dirección\n");
        
        return contenido.toString();
    }

    // ✅ NUEVO: Método para envío de emails HTML (avanzado)
    public void enviarNotificacionHTML(String destinatario, String asunto, String tipoNotificacion, Object data) {
        if (!emailEnabled) {
            System.out.println("📧 Email HTML deshabilitado - " + tipoNotificacion + " para " + destinatario);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            
            // Generar contenido HTML
            String htmlContent = generarContenidoHTML(tipoNotificacion, data);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            System.out.println("✅ Email HTML REAL enviado: " + tipoNotificacion + " a " + destinatario);
            
        } catch (MessagingException e) {
            System.err.println("❌ Error enviando email HTML a " + destinatario + ": " + e.getMessage());
            // Fallback a email simple
            enviarNotificacion(destinatario, "Notificación: " + tipoNotificacion);
        }
    }

    // ✅ GENERAR contenido HTML para emails
    private String generarContenidoHTML(String tipoNotificacion, Object data) {
        StringBuilder html = new StringBuilder();
        
        // HTML básico pero atractivo
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 20px; background-color: #f8fafc; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }");
        html.append(".header { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); color: white; padding: 30px 20px; text-align: center; }");
        html.append(".header h1 { margin: 0; font-size: 28px; font-weight: bold; }");
        html.append(".content { padding: 30px 20px; }");
        html.append(".content h2 { color: #1f2937; margin-top: 0; }");
        html.append(".info-box { background: #f3f4f6; padding: 20px; border-radius: 8px; margin: 20px 0; }");
        html.append(".button { background: #3b82f6; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; display: inline-block; margin: 20px 0; }");
        html.append(".footer { background: #f9fafb; padding: 20px; text-align: center; font-size: 14px; color: #6b7280; border-top: 1px solid #e5e7eb; }");
        html.append("</style></head><body>");
        
        html.append("<div class='container'>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>🏃‍♂️ UnoMas</h1>");
        html.append("<p style='margin: 10px 0 0 0; opacity: 0.9;'>Sistema de Gestión Deportiva</p>");
        html.append("</div>");
        
        // Content
        html.append("<div class='content'>");
        
        switch (tipoNotificacion) {
            case "PARTIDO_NUEVO":
                html.append("<h2>🆕 ¡Nuevo partido disponible!</h2>");
                html.append("<p>Se ha creado un nuevo partido que podría interesarte:</p>");
                break;
            case "PARTIDO_ARMADO":
                html.append("<h2>✅ ¡Partido completo!</h2>");
                html.append("<p>El partido ya tiene suficientes jugadores y está listo:</p>");
                break;
            case "PARTIDO_CONFIRMADO":
                html.append("<h2>🎯 Partido confirmado</h2>");
                html.append("<p>Tu partido ha sido confirmado oficialmente:</p>");
                break;
            case "PARTIDO_INICIADO":
                html.append("<h2>🏃‍♂️ ¡Partido en curso!</h2>");
                html.append("<p>Tu partido ha comenzado. ¡Que lo disfrutes!</p>");
                break;
            case "PARTIDO_FINALIZADO":
                html.append("<h2>🏆 Partido finalizado</h2>");
                html.append("<p>El partido ha terminado. ¡Esperamos que hayas tenido una gran experiencia!</p>");
                break;
            case "PARTIDO_CANCELADO":
                html.append("<h2>❌ Partido cancelado</h2>");
                html.append("<p>Lamentamos informarte que el partido ha sido cancelado:</p>");
                break;
            default:
                html.append("<h2>📢 Notificación UnoMas</h2>");
                html.append("<p>Tienes una nueva notificación:</p>");
        }
        
        // Data box
        if (data != null) {
            html.append("<div class='info-box'>");
            html.append("<strong>Detalles:</strong><br>");
            html.append(data.toString().replace("\n", "<br>"));
            html.append("</div>");
        }
        
        // Call to action
        html.append("<a href='http://localhost:5173/dashboard' class='button'>🎯 Ver en UnoMas</a>");
        
        html.append("</div>");
        
        // Footer
        html.append("<div class='footer'>");
        html.append("Este correo fue enviado automáticamente por UnoMas<br>");
        html.append("📱 <a href='http://localhost:5173'>Abrir aplicación</a> | ");
        html.append("⚙️ <a href='http://localhost:5173/perfil'>Configurar notificaciones</a>");
        html.append("</div>");
        
        html.append("</div></body></html>");
        
        return html.toString();
    }

    // ✅ VERIFICAR configuración de email
    public boolean verificarConfiguracion() {
        try {
            return emailEnabled && mailSender != null;
        } catch (Exception e) {
            System.err.println("⚠️ Configuración de email incorrecta: " + e.getMessage());
            return false;
        }
    }

    // ✅ TEST de configuración
    public String testearConfiguracion() {
        if (!emailEnabled) {
            return "❌ Email deshabilitado en configuración";
        }
        
        if (mailSender == null) {
            return "❌ JavaMailSender no configurado";
        }
        
        try {
            // Crear mensaje de prueba sin enviarlo
            SimpleMailMessage testMessage = new SimpleMailMessage();
            testMessage.setFrom(fromEmail);
            testMessage.setTo("test@test.com");
            testMessage.setSubject("Test");
            testMessage.setText("Test");
            
            return "✅ Configuración de email parece correcta";
        } catch (Exception e) {
            return "❌ Error en configuración: " + e.getMessage();
        }
    }
}