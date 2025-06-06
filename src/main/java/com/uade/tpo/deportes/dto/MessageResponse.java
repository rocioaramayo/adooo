package com.uade.tpo.deportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String mensaje;
    private String estado; // "success", "error", "warning", "info"
    private LocalDateTime timestamp;
    private String detalle;

    public static MessageResponse success(String mensaje) {
        return MessageResponse.builder()
                .mensaje(mensaje)
                .estado("success")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static MessageResponse error(String mensaje, String detalle) {
        return MessageResponse.builder()
                .mensaje(mensaje)
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(detalle)
                .build();
    }

    public static MessageResponse warning(String mensaje) {
        return MessageResponse.builder()
                .mensaje(mensaje)
                .estado("warning")
                .timestamp(LocalDateTime.now())
                .build();
    }
}