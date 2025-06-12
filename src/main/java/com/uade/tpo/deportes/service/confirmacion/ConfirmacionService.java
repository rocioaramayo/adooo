package com.uade.tpo.deportes.service.confirmacion;

import com.uade.tpo.deportes.dto.MessageResponse;
import com.uade.tpo.deportes.entity.Confirmacion;
import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.exceptions.PartidoNoEncontradoException;
import com.uade.tpo.deportes.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.deportes.repository.ConfirmacionRepository;
import com.uade.tpo.deportes.repository.PartidoRepository;
import com.uade.tpo.deportes.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmacionService {

    private final ConfirmacionRepository confirmacionRepository;
    private final PartidoRepository partidoRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public void crearConfirmacionesPendientes(Partido partido) {
        List<Usuario> participantes = partido.getParticipantes();
        
        for (Usuario participante : participantes) {
            if (!confirmacionRepository.existsByPartidoAndUsuario(partido, participante)) {
                Confirmacion confirmacion = Confirmacion.builder()
                        .partido(partido)
                        .usuario(participante)
                        .confirmado(false)
                        .build();
                confirmacionRepository.save(confirmacion);
            }
        }
    }

    @Transactional
    public MessageResponse confirmarParticipacion(String emailUsuario, Long partidoId) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new PartidoNoEncontradoException("Partido no encontrado"));

        Confirmacion confirmacion = confirmacionRepository.findByPartidoAndUsuario(partido, usuario)
                .orElseGet(() -> Confirmacion.builder()
                        .partido(partido)
                        .usuario(usuario)
                        .confirmado(true)
                        .build());

        confirmacion.setConfirmado(true);
        confirmacion.setMotivoRechazo(null);
        confirmacionRepository.save(confirmacion);

        return MessageResponse.success("Participación confirmada exitosamente");
    }

    @Transactional
    public MessageResponse rechazarParticipacion(String emailUsuario, Long partidoId, String motivo) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailUsuario);
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new PartidoNoEncontradoException("Partido no encontrado"));

        Confirmacion confirmacion = confirmacionRepository.findByPartidoAndUsuario(partido, usuario)
                .orElseGet(() -> Confirmacion.builder()
                        .partido(partido)
                        .usuario(usuario)
                        .confirmado(false)
                        .build());

        confirmacion.setConfirmado(false);
        confirmacion.setMotivoRechazo(motivo);
        confirmacionRepository.save(confirmacion);

        return MessageResponse.success("Has rechazado la participación en el partido");
    }

    public boolean todosConfirmaron(Partido partido) {
        long totalParticipantes = partido.getParticipantes().size();
        long confirmacionesPositivas = confirmacionRepository.countByPartidoAndConfirmado(partido, true);
        return totalParticipantes > 0 && totalParticipantes == confirmacionesPositivas;
    }
}