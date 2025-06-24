package com.uade.tpo.deportes.service.partido;

import com.uade.tpo.deportes.dto.*;
import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Ubicacion;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.exceptions.PartidoNoEncontradoException;
import com.uade.tpo.deportes.exceptions.UsuarioNoAutorizadoException;
import com.uade.tpo.deportes.patterns.factory.DeporteFactoryProvider;
import com.uade.tpo.deportes.patterns.observer.NotificadorObserver;
import com.uade.tpo.deportes.patterns.state.*;
import com.uade.tpo.deportes.patterns.strategy.*;
import com.uade.tpo.deportes.repository.DeporteRepository;
import com.uade.tpo.deportes.repository.PartidoRepository;
import com.uade.tpo.deportes.repository.UbicacionRepository;
import com.uade.tpo.deportes.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.deportes.patterns.observer.NotificadorCompletoObserver;
import com.uade.tpo.deportes.patterns.observer.NotificadorDeporteFavoritoObserver;
import com.uade.tpo.deportes.patterns.observer.NotificadorNivelCompatibleObserver;
import com.uade.tpo.deportes.service.comentarios.ComentarioService;
import com.uade.tpo.deportes.service.confirmacion.ConfirmacionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.uade.tpo.deportes.service.partido.NotificacionAsyncService;

@Service
@RequiredArgsConstructor
public class PartidoServiceImpl implements PartidoService {
    @Autowired
    private NotificadorDeporteFavoritoObserver notificadorDeporteFavorito;
    @Autowired  
    private NotificadorNivelCompatibleObserver notificadorNivelCompatible;
    @Autowired
    private ComentarioService comentarioService;
    @Autowired
    private ConfirmacionService confirmacionService;

    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private DeporteRepository deporteRepository;
    
    @Autowired
    private UbicacionRepository ubicacionRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private DeporteFactoryProvider deporteFactoryProvider;
    
    @Autowired
    private NotificadorObserver notificadorObserver;
    
    @Autowired
    private EmparejamientoPorNivelStrategy emparejamientoPorNivel;
    
    @Autowired
    private EmparejamientoPorCercaniaStrategy emparejamientoPorCercania;
    
    @Autowired
    private EmparejamientoPorHistorialStrategy emparejamientoPorHistorial;
     @Autowired
    private NotificadorCompletoObserver notificadorCompletoObserver; //  ESTA ES LA CRÍTICA
    @Autowired
    private NotificacionAsyncService notificacionAsyncService;
    @Override
    @Transactional
    public PartidoResponse crearPartido(String emailOrganizador, CrearPartidoRequest request) {
        // Obtener organizador
        Usuario organizador = usuarioService.obtenerUsuarioPorEmail(emailOrganizador);
        
        // Crear o obtener deporte
        Deporte deporte = deporteRepository.findByTipo(request.getTipoDeporte())
                .orElseGet(() -> crearDeporte(request.getTipoDeporte()));
        
        // Crear ubicación
        Ubicacion ubicacion = Ubicacion.builder()
                .direccion(request.getUbicacion().getDireccion())
                .latitud(request.getUbicacion().getLatitud())
                .longitud(request.getUbicacion().getLongitud())
                .zona(request.getUbicacion().getZona())
                .build();
        ubicacionRepository.save(ubicacion);
        
        // Crear partido
        Partido partido = Partido.builder()
                .deporte(deporte)
                .cantidadJugadoresRequeridos(request.getCantidadJugadoresRequeridos())
                .duracion(request.getDuracion())
                .ubicacion(ubicacion)
                .horario(request.getHorario())
                .organizador(organizador)
                .participantes(new ArrayList<>())
                .estadoActual("NECESITAMOS_JUGADORES")
                .estrategiaActual(request.getEstrategiaEmparejamiento() != null ? 
                    request.getEstrategiaEmparejamiento() : "POR_NIVEL")
                .build();
        // Configurar observers ANTES de guardar
      if (partido.getObservers() == null) {
        partido.setObservers(new ArrayList<>());
        }
        
        // Agregar todos los observers necesarios
        partido.agregarObserver(notificadorObserver);
        partido.agregarObserver(notificadorDeporteFavorito);
        partido.agregarObserver(notificadorNivelCompatible);
        
        // ⭐ CRÍTICO: Agregar el observer completo que maneja todos los casos del TPO
        partido.agregarObserver(notificadorCompletoObserver);
        
        // Configurar estrategia
        configurarEstrategiaInterna(partido, request.getEstrategiaEmparejamiento());
        
        // Guardar partido
        partidoRepository.save(partido);
        
        // Notificar en segundo plano
        notificacionAsyncService.notificarCreacionPartido(partido);
        
        // Responder al usuario inmediatamente
        return mapearAResponse(partido, organizador);
    }

    @Override
    public PartidoResponse obtenerPartido(Long partidoId) {
        Partido partido = obtenerPartidoPorId(partidoId);
        return mapearAResponse(partido, null);
    }

    @Override
    public List<PartidoResponse> obtenerPartidosDelUsuario(String email) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        
        List<Partido> partidosOrganizados = partidoRepository.findByOrganizador(usuario);
        List<Partido> partidosJugados = partidoRepository.findPartidosConJugador(usuario);
        
        // Combinar y eliminar duplicados
        List<Partido> todosLosPartidos = new ArrayList<>(partidosOrganizados);
        partidosJugados.forEach(p -> {
            if (!todosLosPartidos.contains(p)) {
                todosLosPartidos.add(p);
            }
        });
        
        return todosLosPartidos.stream()
                .map(p -> mapearAResponse(p, usuario))
                .collect(Collectors.toList());
    }

@Override
public Page<PartidoResponse> buscarPartidos(String emailUsuario, CriteriosBusqueda criterios, Pageable pageable) {
    Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
    
    System.out.println("🔍 === BÚSQUEDA INTELIGENTE INICIADA ===");
    System.out.println("👤 Usuario: " + usuario.getNombreUsuario() + " (Nivel: " + usuario.getNivelJuego() + ")");
    
    // ⚡ PASO 1: Obtener partidos candidatos
    List<Partido> partidos = obtenerPartidosCandidatos(usuario, criterios);
    System.out.println("📊 Partidos candidatos encontrados: " + partidos.size());
    
    // ⚡ PASO 2: Aplicar filtros
    partidos = aplicarFiltrosInteligentes(partidos, criterios);
    System.out.println("🔧 Partidos después de filtros: " + partidos.size());
    
    // ⚡ PASO 3: Configurar estrategias y calcular compatibilidad
    partidos.forEach(p -> {
        configurarEstrategiaInterna(p, p.getEstrategiaActual());
    });
    
    // ⚡ PASO 4: Ordenar por compatibilidad
    partidos = ordenarPartidosPorCompatibilidad(partidos, criterios, usuario);
    
    // ⚡ PASO 5: Convertir a responses
    List<PartidoResponse> responses = partidos.stream()
            .map(p -> mapearAResponseConCompatibilidad(p, usuario))
            .collect(Collectors.toList());
    
    // ⚡ PASO 6: Paginación
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), responses.size());
    List<PartidoResponse> pageContent = responses.subList(start, end);
    
    System.out.println("📄 Página devuelta: " + pageContent.size() + " partidos");
    System.out.println("🔍 === BÚSQUEDA COMPLETADA ===\n");
    
    return new PageImpl<>(pageContent, pageable, responses.size());
}

// 🎯 MÉTODOS AUXILIARES SIMPLIFICADOS

private List<Partido> obtenerPartidosCandidatos(Usuario usuario, CriteriosBusqueda criterios) {
    LocalDateTime ahora = LocalDateTime.now();
    
    if (criterios.getTipoDeporte() != null) {
        System.out.println("🎾 Búsqueda por deporte: " + criterios.getTipoDeporte());
        return partidoRepository.findPartidosDisponiblesPorDeporte(usuario, criterios.getTipoDeporte(), ahora);
    } else if (criterios.getZona() != null) {
        System.out.println("🗺️ Búsqueda por zona: " + criterios.getZona());
        return partidoRepository.findPartidosDisponiblesPorZona(usuario, criterios.getZona(), ahora);
    } else {
        System.out.println("🌟 Búsqueda general");
        return partidoRepository.findPartidosDisponiblesParaUsuario(usuario, ahora);
    }
}

private List<Partido> aplicarFiltrosInteligentes(List<Partido> partidos, CriteriosBusqueda criterios) {
    return partidos.stream()
            // Filtros temporales
            .filter(p -> criterios.getFechaDesde() == null || p.getHorario().isAfter(criterios.getFechaDesde()))
            .filter(p -> criterios.getFechaHasta() == null || p.getHorario().isBefore(criterios.getFechaHasta()))
            
            // Solo disponibles si se solicita
            .filter(p -> !criterios.isSoloDisponibles() || 
                        p.getParticipantes().size() < p.getCantidadJugadoresRequeridos())
            
            // No partidos muy próximos (menos de 30 min)
            .filter(p -> p.getHorario().isAfter(LocalDateTime.now().plusMinutes(30)))
            
            .collect(Collectors.toList());
}

private List<Partido> ordenarPartidosPorCompatibilidad(List<Partido> partidos, CriteriosBusqueda criterios, Usuario usuario) {
    // Crear mapa de compatibilidades
    Map<Long, Double> compatibilidades = new HashMap<>();
    
    for (Partido partido : partidos) {
        double compatibilidad = 0.0;
        
        // Calcular compatibilidad si hay estrategia
        if (partido.getEstrategiaEmparejamiento() != null) {
            compatibilidad = partido.getEstrategiaEmparejamiento().calcularCompatibilidad(usuario, partido);
        }
        
        // 🎯 BONUS POR DEPORTE FAVORITO
        if (usuario.getDeporteFavorito() != null && 
            usuario.getDeporteFavorito().equals(partido.getDeporte().getTipo())) {
            compatibilidad += 0.1;
        }
        
        // 🕐 BONUS POR HORARIO CONVENIENTE
        if (esHorarioConveniente(partido.getHorario())) {
            compatibilidad += 0.05;
        }
        
        compatibilidad = Math.max(0.0, Math.min(1.0, compatibilidad));
        compatibilidades.put(partido.getId(), compatibilidad);
        
        System.out.println(String.format("🎯 Compatibilidad %s: %.1f%%", 
            partido.getDeporte().getNombre(), compatibilidad * 100));
    }
    
    // Ordenar por compatibilidad
    List<Partido> partidosOrdenados = new ArrayList<>(partidos);
    
    if ("compatibilidad".equals(criterios.getOrdenarPor()) || criterios.getOrdenarPor() == null) {
        partidosOrdenados.sort((p1, p2) -> {
            Double comp1 = compatibilidades.get(p1.getId());
            Double comp2 = compatibilidades.get(p2.getId());
            return comp2.compareTo(comp1); // Descendente
        });
    } else if ("fecha".equals(criterios.getOrdenarPor())) {
        partidosOrdenados.sort(Comparator.comparing(Partido::getHorario));
    }
    
    return partidosOrdenados;
}

private boolean esHorarioConveniente(LocalDateTime horario) {
    int hora = horario.getHour();
    int diaSemana = horario.getDayOfWeek().getValue();
    
    if (diaSemana <= 5) { // Lunes a Viernes
        return hora >= 17 && hora <= 21;
    } else { // Fin de semana
        return hora >= 10 && hora <= 22;
    }
}

private PartidoResponse mapearAResponseConCompatibilidad(Partido partido, Usuario usuario) {
    boolean puedeUnirse = partido.puedeUnirse(usuario);
    
    // Calcular compatibilidad final
    double compatibilidad = 0.0;
    if (partido.getEstrategiaEmparejamiento() != null) {
        compatibilidad = partido.getEstrategiaEmparejamiento().calcularCompatibilidad(usuario, partido);
    }
    
    // Aplicar mismos bonus que en ordenamiento
    if (usuario.getDeporteFavorito() != null && 
        usuario.getDeporteFavorito().equals(partido.getDeporte().getTipo())) {
        compatibilidad += 0.1;
    }
    
    if (esHorarioConveniente(partido.getHorario())) {
        compatibilidad += 0.05;
    }
    
    compatibilidad = Math.max(0.0, Math.min(1.0, compatibilidad));
    
    return PartidoResponse.builder()
            .id(partido.getId())
            .deporte(mapearDeporteAResponse(partido.getDeporte()))
            .cantidadJugadoresRequeridos(partido.getCantidadJugadoresRequeridos())
            .cantidadJugadoresActual(partido.getParticipantes().size())
            .duracion(partido.getDuracion())
            .ubicacion(mapearUbicacionAResponse(partido.getUbicacion()))
            .horario(partido.getHorario())
            .organizador(mapearUsuarioAResponse(partido.getOrganizador()))
            .jugadores(partido.getParticipantes().stream()
                    .map(this::mapearUsuarioAResponse)
                    .collect(Collectors.toList()))
            .estado(partido.getEstadoActual())
            .estrategiaEmparejamiento(partido.getEstrategiaActual())
            .createdAt(partido.getCreatedAt())
            .puedeUnirse(puedeUnirse)
            .compatibilidad(compatibilidad) // ⭐ COMPATIBILIDAD CALCULADA
            .build();
}

    @Override
@Transactional
public MessageResponse unirseAPartido(String emailUsuario, Long partidoId) {
    Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
    Partido partido = obtenerPartidoPorId(partidoId);
    
    // ✅ RECONECTAR OBSERVERS (por si se perdieron al cargar de BD)
    reconectarObservers(partido);
    
    // Configurar estrategia
    configurarEstrategiaInterna(partido, partido.getEstrategiaActual());
    
    // Configurar estado
    EstadoPartido estado = obtenerEstadoPorNombre(partido.getEstadoActual());
    
    try {
        int jugadoresAntes = partido.getParticipantes().size();
        
        // Intentar unirse
        estado.manejarSolicitudUnion(partido, usuario);
        
        // ✅ VERIFICAR SI AHORA ESTÁ COMPLETO (REQUERIMIENTO TPO)
        if (jugadoresAntes < partido.getCantidadJugadoresRequeridos() && 
            partido.getParticipantes().size() >= partido.getCantidadJugadoresRequeridos()) {
            
            // Cambiar estado automáticamente
            partido.cambiarEstado("PARTIDO_ARMADO");
            System.out.println("🎯 Partido " + partidoId + " ahora está ARMADO - Disparando notificaciones");
        }
        
        // Guardar cambios
        partidoRepository.save(partido);
        
        // ✅ NOTIFICAR CAMBIOS
        notificacionAsyncService.notificarCreacionPartido(partido);
        
        return MessageResponse.success("Te has unido al partido exitosamente");
        
    } catch (IllegalArgumentException | IllegalStateException e) {
        return MessageResponse.error("No puedes unirte al partido", e.getMessage());
    }
}

    @Override
    @Transactional  
    public MessageResponse cambiarEstadoPartido(String emailOrganizador, Long partidoId, CambiarEstadoPartidoRequest request) {
        Usuario organizador = usuarioService.obtenerUsuarioPorEmail(emailOrganizador);
        Partido partido = obtenerPartidoPorId(partidoId);
        
        // Permitir que el admin o el organizador cambien el estado
        if (!partido.getOrganizador().equals(organizador) && !organizador.getRole().name().equals("ADMIN")) {
            throw new UsuarioNoAutorizadoException("Solo el organizador o un admin pueden cambiar el estado del partido");
        }
        
        // ✅ RECONECTAR OBSERVERS
        reconectarObservers(partido);
        
        String estadoAnterior = partido.getEstadoActual();
        
        // Cambiar estado
        partido.cambiarEstado(request.getNuevoEstado());
        partidoRepository.save(partido);
        
        // ✅ NOTIFICAR CAMBIO DE ESTADO (REQUERIMIENTO TPO)
        System.out.println("🔔 Estado cambió de " + estadoAnterior + " → " + request.getNuevoEstado() + 
                          " - Disparando notificaciones");
        notificacionAsyncService.notificarCreacionPartido(partido);
    
        // Acciones adicionales por estado
        if ("PARTIDO_ARMADO".equals(request.getNuevoEstado())) {
            confirmacionService.crearConfirmacionesPendientes(partido);
        }
        if ("FINALIZADO".equals(request.getNuevoEstado())) {
            comentarioService.generarEstadisticasAlFinalizar(partido);
        }
        
        return MessageResponse.success("Estado del partido actualizado a: " + request.getNuevoEstado());
    }
/**
 * 🔌 RECONECTAR OBSERVERS - Soluciona problema de pérdida de observers al cargar de BD
 */
private void reconectarObservers(Partido partido) {
    try {
        // Limpiar observers existentes
        if (partido.getObservers() != null) {
            partido.getObservers().clear();
        } else {
            partido.setObservers(new ArrayList<>());
        }
        
        // Reagregar todos los observers necesarios
        partido.agregarObserver(notificadorObserver);
        partido.agregarObserver(notificadorDeporteFavorito);
        partido.agregarObserver(notificadorNivelCompatible);
        partido.agregarObserver(notificadorCompletoObserver); // ⭐ CRÍTICO
        
        System.out.println("🔌 Observers reconectados para partido " + partido.getId());
        
    } catch (Exception e) {
        System.err.println("⚠️ Error reconectando observers: " + e.getMessage());
    }
}
    @Override
    @Transactional
    public MessageResponse configurarEstrategia(Long partidoId, ConfigurarEstrategiaRequest request) {
        Partido partido = obtenerPartidoPorId(partidoId);
        
        partido.setEstrategiaActual(request.getTipoEstrategia());
        configurarEstrategiaInterna(partido, request.getTipoEstrategia());
        
        // Configurar parámetros específicos
        if ("POR_NIVEL".equals(request.getTipoEstrategia()) && partido.getEstrategiaEmparejamiento() instanceof EmparejamientoPorNivelStrategy) {
            EmparejamientoPorNivelStrategy estrategia = (EmparejamientoPorNivelStrategy) partido.getEstrategiaEmparejamiento();
            if (request.getNivelMinimo() != null) {
                estrategia.setNivelMinimo(request.getNivelMinimo());
            }
            if (request.getNivelMaximo() != null) {
                estrategia.setNivelMaximo(request.getNivelMaximo());
            }
        }
        
        partidoRepository.save(partido);
        
        return MessageResponse.success("Estrategia de emparejamiento configurada");
    }

    @Override
    public Partido obtenerPartidoPorId(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new PartidoNoEncontradoException("Partido no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void procesarTransicionesAutomaticas() {
        LocalDateTime ahora = LocalDateTime.now();
        
        // Partidos para iniciar
        List<Partido> partidosParaIniciar = partidoRepository.findPartidosParaIniciar(
            ahora, ahora.minusMinutes(5));
        
        partidosParaIniciar.forEach(p -> {
            p.cambiarEstado("EN_JUEGO");
            partidoRepository.save(p);
        });
        
        // Partidos para finalizar
        List<Partido> partidosParaFinalizar = partidoRepository.findPartidosParaFinalizar(
            ahora.minusMinutes(90)); // Asumiendo duración promedio
        
        partidosParaFinalizar.forEach(p -> {
            p.cambiarEstado("FINALIZADO");
            partidoRepository.save(p);
        });
    }

    // Métodos auxiliares privados
    private Deporte crearDeporte(com.uade.tpo.deportes.enums.TipoDeporte tipo) {
        return deporteFactoryProvider.getFactory(tipo).crearDeporteCompleto(tipo);
    }

    private void configurarEstrategiaInterna(Partido partido, String tipoEstrategia) {
        switch (tipoEstrategia) {
            case "POR_NIVEL":
                partido.setEstrategiaEmparejamiento(emparejamientoPorNivel);
                break;
            case "POR_CERCANIA":
                partido.setEstrategiaEmparejamiento(emparejamientoPorCercania);
                break;
            case "POR_HISTORIAL":
                partido.setEstrategiaEmparejamiento(emparejamientoPorHistorial);
                break;
            default:
                partido.setEstrategiaEmparejamiento(emparejamientoPorNivel);
        }
    }

    private EstadoPartido obtenerEstadoPorNombre(String nombre) {
        switch (nombre) {
            case "NECESITAMOS_JUGADORES":
                return new NecesitamosJugadoresState();
            case "PARTIDO_ARMADO":
                return new PartidoArmadoState();
            case "CONFIRMADO":
                return new ConfirmadoState();
            case "EN_JUEGO":
                return new EnJuegoState();
            case "FINALIZADO":
                return new FinalizadoState();
            case "CANCELADO":
                return new CanceladoState();
            default:
                return new NecesitamosJugadoresState();
        }
    }

    private List<Partido> aplicarFiltros(List<Partido> partidos, CriteriosBusqueda criterios) {
        return partidos.stream()
                .filter(p -> criterios.getFechaDesde() == null || 
                           p.getHorario().isAfter(criterios.getFechaDesde()))
                .filter(p -> criterios.getFechaHasta() == null || 
                           p.getHorario().isBefore(criterios.getFechaHasta()))
                .collect(Collectors.toList());
    }

    private List<Partido> ordenarPartidos(List<Partido> partidos, CriteriosBusqueda criterios, Usuario usuario) {
        Comparator<Partido> comparator = Comparator.comparing(Partido::getHorario);
        
        if ("compatibilidad".equals(criterios.getOrdenarPor())) {
            comparator = Comparator.comparing(p -> 
                p.getEstrategiaEmparejamiento().calcularCompatibilidad(usuario, p), 
                Comparator.reverseOrder());
        }
        
        if ("desc".equals(criterios.getOrden())) {
            comparator = comparator.reversed();
        }
        
        return partidos.stream().sorted(comparator).collect(Collectors.toList());
    }

    private PartidoResponse mapearAResponse(Partido partido, Usuario usuario) {
        boolean puedeUnirse = usuario != null && partido.puedeUnirse(usuario);
        Double compatibilidad = usuario != null && partido.getEstrategiaEmparejamiento() != null ? 
                partido.getEstrategiaEmparejamiento().calcularCompatibilidad(usuario, partido) : 0.0;
        
        return PartidoResponse.builder()
                .id(partido.getId())
                .deporte(mapearDeporteAResponse(partido.getDeporte()))
                .cantidadJugadoresRequeridos(partido.getCantidadJugadoresRequeridos())
                .cantidadJugadoresActual(partido.getParticipantes().size())
                .duracion(partido.getDuracion())
                .ubicacion(mapearUbicacionAResponse(partido.getUbicacion()))
                .horario(partido.getHorario())
                .organizador(mapearUsuarioAResponse(partido.getOrganizador()))
                .jugadores(partido.getParticipantes().stream()
                        .map(this::mapearUsuarioAResponse)
                        .collect(Collectors.toList()))
                .estado(partido.getEstadoActual())
                .estrategiaEmparejamiento(partido.getEstrategiaActual())
                .createdAt(partido.getCreatedAt())
                .puedeUnirse(puedeUnirse)
                .compatibilidad(compatibilidad)
                .build();
    }

    private DeporteResponse mapearDeporteAResponse(Deporte deporte) {
        return DeporteResponse.builder()
                .id(deporte.getId())
                .tipo(deporte.getTipo())
                .nombre(deporte.getNombre())
                .jugadoresPorEquipo(deporte.getJugadoresPorEquipo())
                .reglasBasicas(deporte.getReglasBasicas())
                .build();
    }

    private UbicacionResponse mapearUbicacionAResponse(Ubicacion ubicacion) {
        return UbicacionResponse.builder()
                .id(ubicacion.getId())
                .direccion(ubicacion.getDireccion())
                .latitud(ubicacion.getLatitud())
                .longitud(ubicacion.getLongitud())
                .zona(ubicacion.getZona())
                .build();
    }

    private UsuarioResponse mapearUsuarioAResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .email(usuario.getEmail())
                .deporteFavorito(usuario.getDeporteFavorito())
                .nivelJuego(usuario.getNivelJuego())
                .role(usuario.getRole())
                .activo(usuario.isActivo())
                .createdAt(usuario.getCreatedAt())
                .build();
    }

    public List<PartidoResponse> buscarTodosParaAdmin() {
        List<Partido> partidos = partidoRepository.findAll();
        return partidos.stream()
                .map(p -> mapearAResponse(p, null))
                .collect(Collectors.toList());
    }
}