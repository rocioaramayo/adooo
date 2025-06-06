package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.UbicacionResponse;
import com.uade.tpo.deportes.entity.Ubicacion;
import com.uade.tpo.deportes.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @GetMapping("/zonas")
    public ResponseEntity<List<String>> obtenerZonas() {
        List<String> zonas = ubicacionRepository.findZonasDisponibles();
        return ResponseEntity.ok(zonas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UbicacionResponse>> buscarUbicaciones(@RequestParam String direccion) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findByDireccionContainingIgnoreCase(direccion);
        
        List<UbicacionResponse> responses = ubicaciones.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<UbicacionResponse>> obtenerUbicacionesPorZona(@PathVariable String zona) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findByZona(zona);
        
        List<UbicacionResponse> responses = ubicaciones.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    private UbicacionResponse mapearAResponse(Ubicacion ubicacion) {
        return UbicacionResponse.builder()
                .id(ubicacion.getId())
                .direccion(ubicacion.getDireccion())
                .latitud(ubicacion.getLatitud())
                .longitud(ubicacion.getLongitud())
                .zona(ubicacion.getZona())
                .build();
    }
}