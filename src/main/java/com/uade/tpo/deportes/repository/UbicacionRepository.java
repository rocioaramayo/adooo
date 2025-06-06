package com.uade.tpo.deportes.repository;

import com.uade.tpo.deportes.entity.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    
    List<Ubicacion> findByZona(String zona);
    
    List<Ubicacion> findByDireccionContainingIgnoreCase(String direccion);
    
    // Búsqueda por coordenadas (simplificada)
    @Query("SELECT u FROM Ubicacion u WHERE " +
           "u.latitud BETWEEN :latMin AND :latMax AND " +
           "u.longitud BETWEEN :lonMin AND :lonMax")
    List<Ubicacion> findByCoordenadasEnRango(
        @Param("latMin") Double latMin,
        @Param("latMax") Double latMax,
        @Param("lonMin") Double lonMin,
        @Param("lonMax") Double lonMax
    );
    
    // Búsqueda de zonas disponibles
    @Query("SELECT DISTINCT u.zona FROM Ubicacion u WHERE u.zona IS NOT NULL ORDER BY u.zona")
    List<String> findZonasDisponibles();
}
