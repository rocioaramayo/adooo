package com.uade.tpo.deportes.config;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.enums.NivelJuego;
import com.uade.tpo.deportes.enums.Role;
import com.uade.tpo.deportes.enums.TipoDeporte;
import com.uade.tpo.deportes.patterns.factory.DeporteFactoryProvider;
import com.uade.tpo.deportes.repository.DeporteRepository;
import com.uade.tpo.deportes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DeporteRepository deporteRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private DeporteFactoryProvider deporteFactoryProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        inicializarDeportes();
        inicializarUsuarioAdmin();
    }

    private void inicializarDeportes() {
        // Crear deportes básicos si no existen
        Arrays.stream(TipoDeporte.values()).forEach(tipo -> {
            if (!deporteRepository.existsByTipo(tipo)) {
                Deporte deporte = deporteFactoryProvider.getFactory(tipo).crearDeporteCompleto(tipo);
                deporteRepository.save(deporte);
                System.out.println("Deporte creado: " + deporte.getNombre());
            }
        });
    }

    private void inicializarUsuarioAdmin() {
        // Crear usuario admin si no existe
        if (!usuarioRepository.existsByRole(Role.ADMIN)) {
            Usuario admin = Usuario.builder()
                    .nombreUsuario("admin")
                    .email("admin@unomas.com")
                    .contrasena(passwordEncoder.encode("admin123"))
                    .deporteFavorito(TipoDeporte.FUTBOL)
                    .nivelJuego(NivelJuego.AVANZADO)
                    .role(Role.ADMIN)
                    .activo(true)
                    .build();
            
            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado: admin@unomas.com / admin123");
        }
    }
}