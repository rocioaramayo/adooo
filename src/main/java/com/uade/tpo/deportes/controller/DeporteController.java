package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.DeporteResponse;
import com.uade.tpo.deportes.dto.TipoDeporteResponse;
import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.enums.TipoDeporte;
import com.uade.tpo.deportes.patterns.factory.DeporteFactoryProvider;
import com.uade.tpo.deportes.repository.DeporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/deportes")
@RequiredArgsConstructor
public class DeporteController {

    @Autowired
    private DeporteRepository deporteRepository;
    
    @Autowired
    private DeporteFactoryProvider deporteFactoryProvider;

    @GetMapping
    public ResponseEntity<List<DeporteResponse>> obtenerDeportes() {
        List<Deporte> deportes = deporteRepository.findAll();
        
        // Si no hay deportes, crear los básicos
        if (deportes.isEmpty()) {
            deportes = crearDeportesBasicos();
        }
        
        List<DeporteResponse> responses = deportes.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

 @GetMapping("/tipos")
public ResponseEntity<List<TipoDeporteResponse>> obtenerTiposDeporte() {
    List<TipoDeporteResponse> tipos = Arrays.stream(TipoDeporte.values())
            .map(tipo -> TipoDeporteResponse.builder()
                    .value(tipo.name())      // FUTBOL, BASQUET, etc.
                    .label(tipo.getNombre()) // Fútbol, Básquet, etc.
                    .build())
            .collect(Collectors.toList());
    return ResponseEntity.ok(tipos);
}
    @GetMapping("/{id}")
    public ResponseEntity<DeporteResponse> obtenerDeporte(@PathVariable Long id) {
        return deporteRepository.findById(id)
                .map(this::mapearAResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/crear/{tipo}")
    public ResponseEntity<DeporteResponse> crearDeporte(@PathVariable TipoDeporte tipo) {
        try {
            // Verificar si ya existe
            if (deporteRepository.existsByTipo(tipo)) {
                Deporte existente = deporteRepository.findByTipo(tipo).get();
                return ResponseEntity.ok(mapearAResponse(existente));
            }
            
            // Crear nuevo deporte usando el factory
            Deporte nuevoDeporte = deporteFactoryProvider.getFactory(tipo).crearDeporteCompleto(tipo);
            deporteRepository.save(nuevoDeporte);
            
            return ResponseEntity.ok(mapearAResponse(nuevoDeporte));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private List<Deporte> crearDeportesBasicos() {
        List<Deporte> deportes = Arrays.stream(TipoDeporte.values())
                .map(tipo -> deporteFactoryProvider.getFactory(tipo).crearDeporteCompleto(tipo))
                .collect(Collectors.toList());
        
        return deporteRepository.saveAll(deportes);
    }

    private DeporteResponse mapearAResponse(Deporte deporte) {
        return DeporteResponse.builder()
                .id(deporte.getId())
                .tipo(deporte.getTipo())
                .nombre(deporte.getNombre())
                .jugadoresPorEquipo(deporte.getJugadoresPorEquipo())
                .reglasBasicas(deporte.getReglasBasicas())
                .build();
    }
}