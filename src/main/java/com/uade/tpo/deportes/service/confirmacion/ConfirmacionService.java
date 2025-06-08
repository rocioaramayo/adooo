package com.uade.tpo.deportes.service.confirmacion;

import com.uade.tpo.deportes.dto.MessageResponse;
import com.uade.tpo.deportes.entity.Partido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmacionService {

    @Transactional
    public void crearConfirmacionesPendientes(Partido partido) {
        // TODO: Implementar lógica de confirmaciones
        System.out.println("✅ Confirmaciones creadas para partido: " + partido.getId());
    }

    @Transactional
    public MessageResponse confirmarParticipacion(String emailUsuario, Long partidoId) {
        // TODO: Implementar confirmación
        System.out.println("✅ Usuario " + emailUsuario + " confirmó participación en partido " + partidoId);
        return MessageResponse.success("Participación confirmada exitosamente");
    }

    @Transactional
    public MessageResponse rechazarParticipacion(String emailUsuario, Long partidoId, String motivo) {
        // TODO: Implementar rechazo
        System.out.println("❌ Usuario " + emailUsuario + " rechazó participación en partido " + partidoId);
        return MessageResponse.success("Has rechazado la participación en el partido");
    }

    public boolean todosConfirmaron(Partido partido) {
        // TODO: Implementar verificación
        return false; // Por ahora siempre false
    }
}