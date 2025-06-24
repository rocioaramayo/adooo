package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.ActualizarPerfilRequest;
import com.uade.tpo.deportes.dto.CriteriosBusquedaUsuario;
import com.uade.tpo.deportes.dto.EstadisticasUsuarioResponse;
import com.uade.tpo.deportes.dto.UsuarioResponse;
import com.uade.tpo.deportes.dto.CambiarRolRequest;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(@AuthenticationPrincipal Usuario usuario) {
        UsuarioResponse response = usuarioService.obtenerPerfil(usuario.getEmail());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody ActualizarPerfilRequest request) {
        UsuarioResponse response = usuarioService.actualizarPerfil(usuario.getEmail(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/buscar")
    public ResponseEntity<List<UsuarioResponse>> buscarUsuarios(
            @RequestBody CriteriosBusquedaUsuario criterios,
            @AuthenticationPrincipal Object principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        Usuario usuario;
        if (principal instanceof Usuario) {
            usuario = (Usuario) principal;
        } else if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            usuario = usuarioService.obtenerUsuarioPorEmail(email);
        } else {
            return ResponseEntity.status(401).build();
        }
        // Solo admin puede ver todos los usuarios
        if (!usuario.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        List<UsuarioResponse> usuarios = usuarioService.buscarUsuarios(criterios, usuario.getEmail());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasUsuarioResponse> obtenerEstadisticas(@AuthenticationPrincipal Usuario usuario) {
        EstadisticasUsuarioResponse estadisticas = usuarioService.obtenerEstadisticas(usuario.getEmail());
        return ResponseEntity.ok(estadisticas);
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponse> activarUsuario(@PathVariable Long id) {
        UsuarioResponse response = usuarioService.activarUsuario(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponse> desactivarUsuario(@PathVariable Long id) {
        UsuarioResponse response = usuarioService.desactivarUsuario(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<UsuarioResponse> cambiarRolUsuario(@PathVariable Long id, @RequestBody CambiarRolRequest request) {
        UsuarioResponse response = usuarioService.cambiarRolUsuario(id, request.getRol());
        return ResponseEntity.ok(response);
    }
}