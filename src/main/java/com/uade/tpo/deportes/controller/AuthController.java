package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.LoginRequest;
import com.uade.tpo.deportes.dto.LoginResponse;
import com.uade.tpo.deportes.dto.RegisterRequest;
import com.uade.tpo.deportes.dto.RegisterResponse;
import com.uade.tpo.deportes.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registrarUsuario(@RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = usuarioService.registrarUsuario(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                RegisterResponse.builder()
                    .mensaje("Error en el registro: " + e.getMessage())
                    .build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = usuarioService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                LoginResponse.builder()
                    .mensaje("Error en el login: " + e.getMessage())
                    .build()
            );
        }
    }
}