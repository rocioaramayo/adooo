package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.ActualizarPerfilRequest;
import com.uade.tpo.deportes.dto.CriteriosBusquedaUsuario;
import com.uade.tpo.deportes.dto.EstadisticasUsuarioResponse;
import com.uade.tpo.deportes.dto.UsuarioResponse;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

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
    public ResponseEntity<List<UsuarioResponse>> buscarUsuarios(@RequestBody CriteriosBusquedaUsuario criterios) {
        List<UsuarioResponse> usuarios = usuarioService.buscarUsuarios(criterios);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasUsuarioResponse> obtenerEstadisticas(@AuthenticationPrincipal Usuario usuario) {
        EstadisticasUsuarioResponse estadisticas = usuarioService.obtenerEstadisticas(usuario.getEmail());
        return ResponseEntity.ok(estadisticas);
    }
}