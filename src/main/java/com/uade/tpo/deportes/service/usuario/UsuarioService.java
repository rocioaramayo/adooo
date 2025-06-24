package com.uade.tpo.deportes.service.usuario;

import com.uade.tpo.deportes.dto.*;
import com.uade.tpo.deportes.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    // CORREGIDO: Métodos de autenticación con tipos específicos
    RegisterResponse registrarUsuario(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    
    // Gestión de perfil
    UsuarioResponse obtenerPerfil(String email);
    UsuarioResponse actualizarPerfil(String email, ActualizarPerfilRequest request);
    
    // Búsquedas
    List<UsuarioResponse> buscarUsuarios(CriteriosBusquedaUsuario criterios, String emailUsuario);
    Usuario obtenerUsuarioPorId(Long id);
    Usuario obtenerUsuarioPorEmail(String email);
    
    // Estadísticas
    EstadisticasUsuarioResponse obtenerEstadisticas(String email);

    UsuarioResponse activarUsuario(Long id);
    UsuarioResponse desactivarUsuario(Long id);
    UsuarioResponse cambiarRolUsuario(Long id, String nuevoRol);
}
