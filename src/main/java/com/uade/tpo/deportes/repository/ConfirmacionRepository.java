package com.uade.tpo.deportes.repository;

import com.uade.tpo.deportes.entity.Confirmacion;
import com.uade.tpo.deportes.entity.Partido;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmacionRepository extends JpaRepository<Confirmacion, Long> {
    List<Confirmacion> findByPartido(Partido partido);
    Optional<Confirmacion> findByPartidoAndUsuario(Partido partido, Usuario usuario);
    boolean existsByPartidoAndUsuario(Partido partido, Usuario usuario);
    long countByPartidoAndConfirmado(Partido partido, boolean confirmado);
} 