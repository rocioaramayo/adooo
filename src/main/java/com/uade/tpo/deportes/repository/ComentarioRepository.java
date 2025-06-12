package com.uade.tpo.deportes.repository;

import com.uade.tpo.deportes.entity.Comentario;
import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    Page<Comentario> findByPartidoOrderByFechaCreacionDesc(Partido partido, Pageable pageable);
    List<Comentario> findByPartido(Partido partido);
    Optional<Comentario> findByPartidoAndUsuario(Partido partido, Usuario usuario);
    
    @Query("SELECT AVG(c.calificacion) FROM Comentario c WHERE c.partido = ?1 AND c.calificacion IS NOT NULL")
    Double getPromedioCalificacion(Partido partido);
    
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.partido = ?1 AND c.calificacion IS NOT NULL")
    Long getCantidadCalificaciones(Partido partido);
} 