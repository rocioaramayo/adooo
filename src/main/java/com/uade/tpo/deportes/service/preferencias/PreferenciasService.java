package com.uade.tpo.deportes.service.preferencias;

import com.uade.tpo.deportes.dto.*;
import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.entity.Preferencias;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.repository.DeporteRepository;
import com.uade.tpo.deportes.repository.PreferenciasRepository;
import com.uade.tpo.deportes.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferenciasService {

    private final PreferenciasRepository preferenciasRepository;
    private final DeporteRepository deporteRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public PreferenciasResponse guardarPreferencias(String emailUsuario, PreferenciasRequest request) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
        
        // Obtener deportes favoritos
        List<Deporte> deportesFavoritos = request.getDeportesFavoritosIds().stream()
                .map(id -> deporteRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Deporte no encontrado: " + id)))
                .collect(Collectors.toList());

        // Crear o actualizar preferencias
        Preferencias preferencias = preferenciasRepository.findByUsuario(usuario)
                .orElseGet(() -> Preferencias.builder().usuario(usuario).build());

        preferencias.setDeportesFavoritos(deportesFavoritos);
        preferencias.setRadioBusqueda(request.getRadioBusqueda());
        preferencias.setNotificacionesEmail(request.getNotificacionesEmail());
        preferencias.setNotificacionesPush(request.getNotificacionesPush());
        preferencias.setZonaPreferida(request.getZonaPreferida());

        preferencias = preferenciasRepository.save(preferencias);
        return mapearAResponse(preferencias);
    }

    public PreferenciasResponse obtenerPreferencias(String emailUsuario) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
        Preferencias preferencias = preferenciasRepository.findByUsuario(usuario)
                .orElseGet(() -> Preferencias.builder()
                        .usuario(usuario)
                        .notificacionesEmail(true)
                        .notificacionesPush(true)
                        .build());
        return mapearAResponse(preferencias);
    }

    private PreferenciasResponse mapearAResponse(Preferencias preferencias) {
        List<DeporteResponse> deportesFavoritos = preferencias.getDeportesFavoritos() != null ?
                preferencias.getDeportesFavoritos().stream()
                        .map(deporte -> DeporteResponse.builder()
                                .id(deporte.getId())
                                .nombre(deporte.getNombre())
                                .build())
                        .collect(Collectors.toList()) :
                List.of();

        return PreferenciasResponse.builder()
                .id(preferencias.getId())
                .usuarioId(preferencias.getUsuario().getId())
                .deportesFavoritos(deportesFavoritos)
                .radioBusqueda(preferencias.getRadioBusqueda())
                .notificacionesEmail(preferencias.getNotificacionesEmail())
                .notificacionesPush(preferencias.getNotificacionesPush())
                .zonaPreferida(preferencias.getZonaPreferida())
                .build();
    }
} 