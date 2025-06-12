package com.uade.tpo.deportes.repository;

import com.uade.tpo.deportes.entity.Preferencias;
import com.uade.tpo.deportes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferenciasRepository extends JpaRepository<Preferencias, Long> {
    Optional<Preferencias> findByUsuario(Usuario usuario);
    boolean existsByUsuario(Usuario usuario);
} 