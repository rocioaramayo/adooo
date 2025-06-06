package com.uade.tpo.deportes.patterns.state;

import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class PartidoArmadoState implements EstadoPartido {

    @Override
    public void manejarSolicitudUnion(Partido partido, Usuario usuario) {
        throw new IllegalStateException("El partido ya está completo");
    }

    @Override
    public void verificarTransicion(Partido partido) {
        // En este estado, esperamos confirmación manual
        // La transición se hace desde el controlador
    }

    @Override
    public EstadoPartido obtenerEstadoSiguiente() {
        return new ConfirmadoState();
    }

    @Override
    public String getNombre() {
        return "PARTIDO_ARMADO";
    }
}
