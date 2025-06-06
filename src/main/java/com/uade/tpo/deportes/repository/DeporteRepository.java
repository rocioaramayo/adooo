package com.uade.tpo.deportes.repository;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.enums.TipoDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeporteRepository extends JpaRepository<Deporte, Long> {
    
    Optional<Deporte> findByTipo(TipoDeporte tipo);
    
    Optional<Deporte> findByNombreIgnoreCase(String nombre);
    
    boolean existsByTipo(TipoDeporte tipo);
}