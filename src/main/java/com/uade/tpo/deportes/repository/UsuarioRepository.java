package com.uade.tpo.deportes.repository;

import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.enums.NivelJuego;
import com.uade.tpo.deportes.enums.Role;
import com.uade.tpo.deportes.enums.TipoDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Métodos de autenticación (igual que en tu código actual)
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByEmail(String email);
    boolean existsByRole(Role role);
    boolean existsByEmail(String email);
    boolean existsByNombreUsuario(String nombreUsuario);
    
    // Métodos específicos del dominio deportivo
    List<Usuario> findByDeporteFavoritoAndActivoTrue(TipoDeporte deporte);
    List<Usuario> findByNivelJuegoAndActivoTrue(NivelJuego nivel);
    List<Usuario> findByDeporteFavoritoAndNivelJuegoAndActivoTrue(
        TipoDeporte deporte, 
        NivelJuego nivel
    );
    
    // Consultas personalizadas para estadísticas
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    long countUsuariosActivos();
    
    @Query("SELECT u.deporteFavorito, COUNT(u) FROM Usuario u WHERE u.activo = true GROUP BY u.deporteFavorito")
    List<Object[]> contarUsuariosPorDeporte();
    
    @Query("SELECT u.nivelJuego, COUNT(u) FROM Usuario u WHERE u.activo = true GROUP BY u.nivelJuego")
    List<Object[]> contarUsuariosPorNivel();
}