package com.uade.tpo.deportes.service.partido;

import com.uade.tpo.deportes.dto.*;
import com.uade.tpo.deportes.entity.Partido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartidoService {
    // CRUD básico
    PartidoResponse crearPartido(String emailOrganizador, CrearPartidoRequest request);
    PartidoResponse obtenerPartido(Long partidoId);
    List<PartidoResponse> obtenerPartidosDelUsuario(String email);
    
    // Funcionalidad principal
    Page<PartidoResponse> buscarPartidos(String emailUsuario, CriteriosBusqueda criterios, Pageable pageable);
    MessageResponse unirseAPartido(String emailUsuario, Long partidoId);
    MessageResponse cambiarEstadoPartido(String emailOrganizador, Long partidoId, CambiarEstadoPartidoRequest request);
    
    // Configuración
    MessageResponse configurarEstrategia(Long partidoId, ConfigurarEstrategiaRequest request);
    
    // Para el sistema interno
    Partido obtenerPartidoPorId(Long id);
    void procesarTransicionesAutomaticas();

    List<PartidoResponse> buscarTodosParaAdmin();
}