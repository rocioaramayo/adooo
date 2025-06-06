package com.uade.tpo.deportes.exceptions;

import com.uade.tpo.deportes.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Argumento inválido")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MessageResponse> handleIllegalState(IllegalStateException ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Estado inválido")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGenericException(Exception ex, WebRequest request) {
        MessageResponse response = MessageResponse.builder()
                .mensaje("Error interno del servidor")
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle("Ha ocurrido un error inesperado")
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}