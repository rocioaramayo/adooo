package com.uade.tpo.deportes.service.pushtoken;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio TEMPORAL para tokens push - Implementación en memoria
 * TODO: Migrar a base de datos cuando esté listo
 */
@Service
public class PushTokenService {

    // ✅ TEMPORAL: Almacenar en memoria hasta crear la entidad
    private final Map<Long, List<String>> tokensEnMemoria = new HashMap<>();

    /**
     * Registrar un nuevo token push para un usuario
     */
    public void registrarToken(Long usuarioId, String token, String deviceType) {
        tokensEnMemoria.computeIfAbsent(usuarioId, k -> new ArrayList<>()).add(token);
        System.out.println("✅ Token push registrado (en memoria) para usuario " + usuarioId + ": " + deviceType);
    }

    /**
     * Obtener todos los tokens activos de un usuario
     */
    public List<String> obtenerTokensUsuario(Long usuarioId) {
        return tokensEnMemoria.getOrDefault(usuarioId, new ArrayList<>());
    }

    /**
     * Obtener tokens de usuarios con un deporte favorito específico
     * TODO: Implementar cuando tengamos la entidad
     */
    public List<String> obtenerTokensPorDeporteFavorito(String tipoDeporte) {
        System.out.println("⚠️ obtenerTokensPorDeporteFavorito - Implementación temporal");
        return new ArrayList<>(); // Por ahora vacío
    }

    /**
     * Desactivar un token (cuando falla el envío o el usuario se desconecta)
     */
    public void desactivarToken(String token) {
        tokensEnMemoria.values().forEach(tokens -> tokens.remove(token));
        System.out.println("⚠️ Token desactivado (en memoria): " + token);
    }

    /**
     * Eliminar tokens antiguos (limpieza periódica)
     */
    public void limpiarTokensAntiguos() {
        System.out.println("🧹 Limpieza de tokens - Implementación temporal");
        // Por ahora no hacer nada
    }

    /**
     * Actualizar fecha de último uso de un token
     */
    public void actualizarUltimoUso(String token) {
        System.out.println("📅 actualizarUltimoUso - Implementación temporal para: " + token);
        // Por ahora no hacer nada
    }

    /**
     * Obtener estadísticas de tokens
     */
    public TokenStats obtenerEstadisticas() {
        long totalTokens = tokensEnMemoria.values().stream().mapToLong(List::size).sum();
        long usuariosConTokens = tokensEnMemoria.size();
        
        return TokenStats.builder()
            .totalTokens(totalTokens)
            .tokensActivos(totalTokens)
            .usuariosConTokens(usuariosConTokens)
            .build();
    }

    /**
     * Datos de estadísticas - CLASE INTERNA SIMPLIFICADA
     */
    public static class TokenStats {
        private long totalTokens;
        private long tokensActivos;
        private long usuariosConTokens;

        // Constructor vacío
        public TokenStats() {}

        // Constructor con parámetros
        public TokenStats(long totalTokens, long tokensActivos, long usuariosConTokens) {
            this.totalTokens = totalTokens;
            this.tokensActivos = tokensActivos;
            this.usuariosConTokens = usuariosConTokens;
        }

        public static TokenStatsBuilder builder() {
            return new TokenStatsBuilder();
        }

        // Getters
        public long getTotalTokens() { return totalTokens; }
        public long getTokensActivos() { return tokensActivos; }
        public long getUsuariosConTokens() { return usuariosConTokens; }

        // Setters
        public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
        public void setTokensActivos(long tokensActivos) { this.tokensActivos = tokensActivos; }
        public void setUsuariosConTokens(long usuariosConTokens) { this.usuariosConTokens = usuariosConTokens; }

        public static class TokenStatsBuilder {
            private long totalTokens;
            private long tokensActivos;
            private long usuariosConTokens;

            public TokenStatsBuilder totalTokens(long totalTokens) {
                this.totalTokens = totalTokens;
                return this;
            }

            public TokenStatsBuilder tokensActivos(long tokensActivos) {
                this.tokensActivos = tokensActivos;
                return this;
            }

            public TokenStatsBuilder usuariosConTokens(long usuariosConTokens) {
                this.usuariosConTokens = usuariosConTokens;
                return this;
            }

            public TokenStats build() {
                return new TokenStats(totalTokens, tokensActivos, usuariosConTokens);
            }
        }
    }
}