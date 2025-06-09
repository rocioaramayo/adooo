package com.uade.tpo.deportes.exceptions;

import com.uade.tpo.deportes.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<MessageResponse> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Usuario no encontrado")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PartidoNoEncontradoException.class)
    public ResponseEntity<MessageResponse> handlePartidoNoEncontrado(PartidoNoEncontradoException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Partido no encontrado")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<MessageResponse> handleUsuarioYaExiste(UsuarioYaExisteException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Usuario ya existe")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailInvalidoException.class)
    public ResponseEntity<MessageResponse> handleEmailInvalido(EmailInvalidoException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Email inválido")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNoAutorizadoException.class)
    public ResponseEntity<MessageResponse> handleUsuarioNoAutorizado(UsuarioNoAutorizadoException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Usuario no autorizado")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PartidoCompletoException.class)
    public ResponseEntity<MessageResponse> handlePartidoCompleto(PartidoCompletoException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Partido completo")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EstadoPartidoInvalidoException.class)
    public ResponseEntity<MessageResponse> handleEstadoPartidoInvalido(EstadoPartidoInvalidoException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Estado del partido inválido")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Credenciales inválidas")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle("Email o contraseña incorrectos")
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // ✅ NUEVO: Manejo específico para errores de base de datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String mensaje = "Error de integridad de datos";
        String detalle = ex.getMessage();
        
        // Analizar el tipo de error específico
        if (detalle.contains("not-null")) {
            mensaje = "Campo requerido faltante";
            detalle = "Uno o más campos obligatorios están vacíos";
        } else if (detalle.contains("unique") || detalle.contains("duplicate")) {
            mensaje = "Datos duplicados";
            detalle = "Ya existe un registro con estos datos";
        } else if (detalle.contains("foreign key")) {
            mensaje = "Referencia inválida";
            detalle = "Hay una referencia a datos que no existen";
        }
        
        System.err.println("🔍 DataIntegrityViolationException: " + ex.getMessage());
        
        MessageResponse response = MessageResponse.builder()
                .mensaje(mensaje)
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(detalle)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ✅ MEJORADO: Manejo más específico para IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        System.err.println("🔍 IllegalArgumentException: " + ex.getMessage());
        
        MessageResponse response = MessageResponse.builder()
                .mensaje("Datos inválidos")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MessageResponse> handleIllegalState(IllegalStateException ex, WebRequest request) {
        System.err.println("🔍 IllegalStateException: " + ex.getMessage());
        
        MessageResponse response = MessageResponse.builder()
                .mensaje("Estado inválido")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // ✅ NUEVO: Manejo para errores de validación de Spring
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder detalles = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            detalles.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        });
        
        MessageResponse response = MessageResponse.builder()
                .mensaje("Errores de validación")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(detalles.toString())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ✅ MEJORADO: Manejo genérico con más logging
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGenericException(Exception ex, WebRequest request) {
        // Log detallado para debugging
        System.err.println("🔥 ERROR INESPERADO:");
        System.err.println("Tipo: " + ex.getClass().getSimpleName());
        System.err.println("Mensaje: " + ex.getMessage());
        System.err.println("Request: " + request.getDescription(false));
        ex.printStackTrace();
        
        MessageResponse response = MessageResponse.builder()
                .mensaje("Error interno del servidor")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle("Ha ocurrido un error inesperado. Revisa los logs del servidor para más detalles.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}