package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.PreferenciasRequest;
import com.uade.tpo.deportes.dto.PreferenciasResponse;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.service.preferencias.PreferenciasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/preferencias")
@RequiredArgsConstructor
public class PreferenciasController {

    private final PreferenciasService preferenciasService;

    @GetMapping
    public ResponseEntity<PreferenciasResponse> obtenerPreferencias(
            @AuthenticationPrincipal Usuario usuario) {
        PreferenciasResponse response = preferenciasService.obtenerPreferencias(usuario.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PreferenciasResponse> guardarPreferencias(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody PreferenciasRequest request) {
        PreferenciasResponse response = preferenciasService.guardarPreferencias(usuario.getEmail(), request);
        return ResponseEntity.ok(response);
    }
} 