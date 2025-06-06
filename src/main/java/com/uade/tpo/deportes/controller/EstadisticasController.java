package com.uade.tpo.deportes.controller;

import com.uade.tpo.deportes.dto.EstadisticasGeneralesResponse;
import com.uade.tpo.deportes.service.estadisticas.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerEstadisticasGenerales() {
        EstadisticasGeneralesResponse estadisticas = estadisticasService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }
}