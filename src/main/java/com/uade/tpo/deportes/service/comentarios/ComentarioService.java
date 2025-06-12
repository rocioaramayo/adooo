package com.uade.tpo.deportes.service.comentarios;

import com.uade.tpo.deportes.dto.*;
import com.uade.tpo.deportes.entity.Comentario;
import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.exceptions.PartidoNoEncontradoException;
import com.uade.tpo.deportes.repository.ComentarioRepository;
import com.uade.tpo.deportes.repository.PartidoRepository;
import com.uade.tpo.deportes.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PartidoRepository partidoRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public MessageResponse agregarComentario(String emailUsuario, AgregarComentarioRequest request) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
        Partido partido = partidoRepository.findById(request.getPartidoId())
                .orElseThrow(() -> new PartidoNoEncontradoException("Partido no encontrado"));

        // Verificar si el usuario ya comentó
        if (comentarioRepository.findByPartidoAndUsuario(partido, usuario).isPresent()) {
            return MessageResponse.error("Ya has comentado este partido", "El usuario ya ha dejado un comentario para este partido.");
        }

        Comentario comentario = Comentario.builder()
                .partido(partido)
                .usuario(usuario)
                .contenido(request.getContenido())
                .calificacion(request.getCalificacion())
                .build();

        comentarioRepository.save(comentario);
        return MessageResponse.success("Comentario agregado exitosamente");
    }

    public Page<ComentarioResponse> obtenerComentariosPartido(Long partidoId, Pageable pageable) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new PartidoNoEncontradoException("Partido no encontrado"));

        Page<Comentario> comentarios = comentarioRepository.findByPartidoOrderByFechaCreacionDesc(partido, pageable);
        
        List<ComentarioResponse> comentariosResponse = comentarios.getContent().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(comentariosResponse, pageable, comentarios.getTotalElements());
    }

    public EstadisticasPartidoDetalleResponse obtenerEstadisticasPartido(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new PartidoNoEncontradoException("Partido no encontrado"));

        Double promedioCalificacion = comentarioRepository.getPromedioCalificacion(partido);
        Long cantidadCalificaciones = comentarioRepository.getCantidadCalificaciones(partido);

        return EstadisticasPartidoDetalleResponse.builder()
                .partidoId(partidoId)
                .promedioCalificacion(promedioCalificacion != null ? promedioCalificacion : 0.0)
                .cantidadCalificaciones(cantidadCalificaciones != null ? cantidadCalificaciones : 0L)
                .build();
    }

    @Transactional
    public void generarEstadisticasAlFinalizar(Partido partido) {
        // Esta función se llama cuando el partido finaliza
        // Podríamos implementar lógica adicional aquí si es necesario
        // Por ejemplo, actualizar estadísticas de usuarios, enviar notificaciones, etc.
        System.out.println("📊 Estadísticas generadas para partido finalizado: " + partido.getId());
    }

    private ComentarioResponse mapearAResponse(Comentario comentario) {
        return ComentarioResponse.builder()
                .id(comentario.getId())
                .partidoId(comentario.getPartido().getId())
                .usuarioId(comentario.getUsuario().getId())
                .nombreUsuario(comentario.getUsuario().getNombreUsuario())
                .contenido(comentario.getContenido())
                .calificacion(comentario.getCalificacion())
                .fechaCreacion(comentario.getFechaCreacion())
                .build();
    }
}