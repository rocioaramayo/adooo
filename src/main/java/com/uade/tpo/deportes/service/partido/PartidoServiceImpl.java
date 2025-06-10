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
import com.uade.tpo.deportes.patterns.observer.NotificadorDeporteFavoritoObserver;
import com.uade.tpo.deportes.patterns.observer.NotificadorNivelCompatibleObserver;
import com.uade.tpo.deportes.service.comentarios.ComentarioService;
import com.uade.tpo.deportes.service.confirmacion.ConfirmacionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
                .jugadores(new ArrayList<>())
                .estadoActual("NECESITAMOS_JUGADORES")
                .estrategiaActual(request.getEstrategiaEmparejamiento() != null ? 
                    request.getEstrategiaEmparejamiento() : "POR_NIVEL")
                .build();
        
        // Configurar observers
        partido.agregarObserver(notificadorObserver);
        partido.agregarObserver(notificadorDeporteFavorito);
        partido.agregarObserver(notificadorNivelCompatible);
        
        // Configurar estrategia
        configurarEstrategiaInterna(partido, request.getEstrategiaEmparejamiento());
        
        // Guardar partido
        partidoRepository.save(partido);
        
        // Notificar creación
        partido.notificarObservers();
        
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
        
        // Obtener partidos disponibles
        List<Partido> partidos;
        
        if (criterios.getTipoDeporte() != null) {
            partidos = partidoRepository.findPartidosDisponiblesPorDeporte(
                usuario, criterios.getTipoDeporte(), LocalDateTime.now());
        } else if (criterios.getZona() != null) {
            partidos = partidoRepository.findPartidosDisponiblesPorZona(
                usuario, criterios.getZona(), LocalDateTime.now());
        } else {
            partidos = partidoRepository.findPartidosDisponiblesParaUsuario(
                usuario, LocalDateTime.now());
        }
        
        // Aplicar filtros adicionales
        partidos = aplicarFiltros(partidos, criterios);
        
        // Configurar estrategias y calcular compatibilidad
        partidos.forEach(p -> {
            configurarEstrategiaInterna(p, p.getEstrategiaActual());
        });
        
        // Ordenar según criterios
        partidos = ordenarPartidos(partidos, criterios, usuario);
        
        // Convertir a responses
        List<PartidoResponse> responses = partidos.stream()
                .map(p -> mapearAResponse(p, usuario))
                .collect(Collectors.toList());
        
        // Aplicar paginación manual
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<PartidoResponse> pageContent = responses.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, responses.size());
    }

    @Override
    @Transactional
    public MessageResponse unirseAPartido(String emailUsuario, Long partidoId) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
        Partido partido = obtenerPartidoPorId(partidoId);
        
        // Configurar estrategia
        configurarEstrategiaInterna(partido, partido.getEstrategiaActual());
        
        // Configurar estado
        EstadoPartido estado = obtenerEstadoPorNombre(partido.getEstadoActual());
        
        try {
            // Intentar unirse
            estado.manejarSolicitudUnion(partido, usuario);
            
            // Guardar cambios
            partidoRepository.save(partido);
            
            return MessageResponse.success("Te has unido al partido exitosamente");
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            return MessageResponse.error("No puedes unirte al partido", e.getMessage());
        }
    }

    @Override
    @Transactional
    public MessageResponse cambiarEstadoPartido(String emailOrganizador, Long partidoId, 
                                              CambiarEstadoPartidoRequest request) {
        Usuario organizador = usuarioService.obtenerUsuarioPorEmail(emailOrganizador);
        Partido partido = obtenerPartidoPorId(partidoId);
        
        // Verificar que es el organizador
        if (!partido.getOrganizador().equals(organizador)) {
            throw new UsuarioNoAutorizadoException("Solo el organizador puede cambiar el estado del partido");
        }
        
        // Cambiar estado
        partido.cambiarEstado(request.getNuevoEstado());
        partidoRepository.save(partido);

        if ("PARTIDO_ARMADO".equals(request.getNuevoEstado())) {
            confirmacionService.crearConfirmacionesPendientes(partido);
        }
        if ("FINALIZADO".equals(request.getNuevoEstado())) {
            comentarioService.generarEstadisticasAlFinalizar(partido);
        }
        
        return MessageResponse.success("Estado del partido actualizado a: " + request.getNuevoEstado());
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
                .cantidadJugadoresActual(partido.getJugadores().size())
                .duracion(partido.getDuracion())
                .ubicacion(mapearUbicacionAResponse(partido.getUbicacion()))
                .horario(partido.getHorario())
                .organizador(mapearUsuarioAResponse(partido.getOrganizador()))
                .jugadores(partido.getJugadores().stream()
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
}