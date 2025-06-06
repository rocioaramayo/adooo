package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.*;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.service.partido.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/partidos")
@RequiredArgsConstructor
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @PostMapping
    public ResponseEntity<PartidoResponse> crearPartido(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody CrearPartidoRequest request) {
        PartidoResponse response = partidoService.crearPartido(usuario.getEmail(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidoResponse> obtenerPartido(@PathVariable Long id) {
        PartidoResponse response = partidoService.obtenerPartido(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mis-partidos")
    public ResponseEntity<List<PartidoResponse>> obtenerMisPartidos(@AuthenticationPrincipal Usuario usuario) {
        List<PartidoResponse> partidos = partidoService.obtenerPartidosDelUsuario(usuario.getEmail());
        return ResponseEntity.ok(partidos);
    }

    @PostMapping("/buscar")
    public ResponseEntity<Page<PartidoResponse>> buscarPartidos(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody CriteriosBusqueda criterios,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PartidoResponse> partidos = partidoService.buscarPartidos(usuario.getEmail(), criterios, pageable);
        return ResponseEntity.ok(partidos);
    }

    @PostMapping("/{id}/unirse")
    public ResponseEntity<MessageResponse> unirseAPartido(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id) {
        MessageResponse response = partidoService.unirseAPartido(usuario.getEmail(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<MessageResponse> cambiarEstadoPartido(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id,
            @RequestBody CambiarEstadoPartidoRequest request) {
        MessageResponse response = partidoService.cambiarEstadoPartido(usuario.getEmail(), id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estrategia")
    public ResponseEntity<MessageResponse> configurarEstrategia(
            @PathVariable Long id,
            @RequestBody ConfigurarEstrategiaRequest request) {
        MessageResponse response = partidoService.configurarEstrategia(id, request);
        return ResponseEntity.ok(response);
    }
}