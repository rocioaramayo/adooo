package com.uade.tpo.deportes.service.comentarios;

import com.uade.tpo.deportes.dto.*;
import com.uade.tpo.deportes.entity.Partido;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    @Transactional
    public MessageResponse agregarComentario(String emailUsuario, AgregarComentarioRequest request) {
        // TODO: Implementar agregar comentario
        System.out.println("✅ Comentario agregado por " + emailUsuario + " en partido " + request.getPartidoId());
        return MessageResponse.success("Comentario agregado exitosamente");
    }

    public Page<ComentarioResponse> obtenerComentariosPartido(Long partidoId, Pageable pageable) {
        // TODO: Implementar obtención de comentarios
        System.out.println("📋 Obteniendo comentarios para partido " + partidoId);
        List<ComentarioResponse> comentarios = new ArrayList<>(); // Por ahora lista vacía
        return new PageImpl<>(comentarios, pageable, 0);
    }

    public EstadisticasPartidoDetalleResponse obtenerEstadisticasPartido(Long partidoId) {
        // TODO: Implementar estadísticas
        System.out.println("📊 Obteniendo estadísticas para partido " + partidoId);
        return EstadisticasPartidoDetalleResponse.builder()
                .partidoId(partidoId)
                .mensaje("Estadísticas en desarrollo")
                .build();
    }

    @Transactional
    public void generarEstadisticasAlFinalizar(Partido partido) {
        // TODO: Implementar generación de estadísticas
        System.out.println("📊 Generando estadísticas para partido finalizado: " + partido.getId());
    }
}