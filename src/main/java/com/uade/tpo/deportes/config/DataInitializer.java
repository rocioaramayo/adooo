package com.uade.tpo.deportes.config;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.entity.Ubicacion;
import com.uade.tpo.deportes.entity.Usuario;
import com.uade.tpo.deportes.enums.NivelJuego;
import com.uade.tpo.deportes.enums.Role;
import com.uade.tpo.deportes.enums.TipoDeporte;
import com.uade.tpo.deportes.patterns.factory.DeporteFactoryProvider;
import com.uade.tpo.deportes.repository.DeporteRepository;
import com.uade.tpo.deportes.repository.UbicacionRepository;
import com.uade.tpo.deportes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DeporteRepository deporteRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UbicacionRepository ubicacionRepository;
    
    @Autowired
    private DeporteFactoryProvider deporteFactoryProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        inicializarDeportes();
        inicializarUsuarioAdmin();
        inicializarZonasYUbicaciones();
        inicializarUsuariosDePrueba();
    }

    private void inicializarDeportes() {
        // Crear deportes básicos si no existen
        Arrays.stream(TipoDeporte.values()).forEach(tipo -> {
            if (!deporteRepository.existsByTipo(tipo)) {
                Deporte deporte = deporteFactoryProvider.getFactory(tipo).crearDeporteCompleto(tipo);
                deporteRepository.save(deporte);
                System.out.println("✅ Deporte creado: " + deporte.getNombre());
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
            System.out.println("✅ Usuario admin creado: admin@unomas.com / admin123");
        }
    }

    private void inicializarZonasYUbicaciones() {
        // Solo inicializar si no hay ubicaciones existentes
        if (ubicacionRepository.count() > 0) {
            System.out.println("🔍 Ubicaciones ya existen, saltando inicialización...");
            return;
        }

        System.out.println("🗺️ Inicializando zonas y ubicaciones...");

        List<Ubicacion> ubicaciones = Arrays.asList(
            // CAPITAL FEDERAL
            crearUbicacion("Plaza de Mayo, CABA", -34.6083, -58.3712, "Centro"),
            crearUbicacion("Puerto Madero, CABA", -34.6118, -58.3631, "Puerto Madero"),
            crearUbicacion("Palermo, CABA", -34.5795, -58.4198, "Palermo"),
            crearUbicacion("Belgrano, CABA", -34.5633, -58.4533, "Belgrano"),
            crearUbicacion("Villa Crespo, CABA", -34.6014, -58.4370, "Villa Crespo"),
            crearUbicacion("Caballito, CABA", -34.6186, -58.4462, "Caballito"),
            crearUbicacion("Flores, CABA", -34.6281, -58.4685, "Flores"),
            crearUbicacion("La Boca, CABA", -34.6345, -58.3617, "La Boca"),
            crearUbicacion("San Telmo, CABA", -34.6214, -58.3731, "San Telmo"),
            crearUbicacion("Recoleta, CABA", -34.5889, -58.3958, "Recoleta"),

            // ZONA NORTE
            crearUbicacion("San Isidro, Buenos Aires", -34.4708, -58.5128, "Zona Norte"),
            crearUbicacion("Vicente López, Buenos Aires", -34.5262, -58.4703, "Zona Norte"),
            crearUbicacion("Olivos, Buenos Aires", -34.5089, -58.4936, "Zona Norte"),
            crearUbicacion("Martínez, Buenos Aires", -34.4918, -58.5058, "Zona Norte"),
            crearUbicacion("Tigre, Buenos Aires", -34.4264, -58.5797, "Zona Norte"),

            // ZONA OESTE
            crearUbicacion("Morón, Buenos Aires", -34.6534, -58.6198, "Zona Oeste"),
            crearUbicacion("Hurlingham, Buenos Aires", -34.5881, -58.6344, "Zona Oeste"),
            crearUbicacion("Ituzaingó, Buenos Aires", -34.6583, -58.6742, "Zona Oeste"),
            crearUbicacion("Ramos Mejía, Buenos Aires", -34.6420, -58.5644, "Zona Oeste"),
            crearUbicacion("Castelar, Buenos Aires", -34.6550, -58.6467, "Zona Oeste"),

            // ZONA SUR
            crearUbicacion("Avellaneda, Buenos Aires", -34.6637, -58.3623, "Zona Sur"),
            crearUbicacion("Quilmes, Buenos Aires", -34.7206, -58.2543, "Zona Sur"),
            crearUbicacion("Lanús, Buenos Aires", -34.7069, -58.3930, "Zona Sur"),
            crearUbicacion("Lomas de Zamora, Buenos Aires", -34.7599, -58.4044, "Zona Sur"),
            crearUbicacion("Banfield, Buenos Aires", -34.7441, -58.3906, "Zona Sur"),

            // ZONA CENTRAL (GBA)
            crearUbicacion("San Martín, Buenos Aires", -34.5735, -58.5370, "Zona Central"),
            crearUbicacion("Tres de Febrero, Buenos Aires", -34.5989, -58.5678, "Zona Central"),
            crearUbicacion("La Matanza, Buenos Aires", -34.7700, -58.6250, "Zona Central"),

            // UBICACIONES SIN COORDENADAS (solo direcciones)
            crearUbicacionSinCoordenadas("Club Estudiantes, La Plata", "La Plata"),
            crearUbicacionSinCoordenadas("Polideportivo Municipal, Pilar", "Zona Norte"),
            crearUbicacionSinCoordenadas("Centro Deportivo, Ezeiza", "Zona Sur"),
            crearUbicacionSinCoordenadas("Complejo Deportivo, Moreno", "Zona Oeste"),
            crearUbicacionSinCoordenadas("Cancha Municipal, San Fernando", "Zona Norte")
        );

        ubicacionRepository.saveAll(ubicaciones);
        System.out.println("✅ Ubicaciones inicializadas: " + ubicaciones.size() + " ubicaciones creadas");
        
        // Mostrar resumen de zonas
        List<String> zonas = ubicacionRepository.findZonasDisponibles();
        System.out.println("🗺️ Zonas disponibles: " + String.join(", ", zonas));
    }

    private void inicializarUsuariosDePrueba() {
        // Solo crear si no existen usuarios de prueba
        if (usuarioRepository.count() > 1) { // Más que solo el admin
            System.out.println("👥 Usuarios de prueba ya existen, saltando...");
            return;
        }

        System.out.println("👥 Creando usuarios de prueba...");

        List<Usuario> usuariosPrueba = Arrays.asList(
            crearUsuarioPrueba("juan.perez", "juan@ejemplo.com", "password123", 
                TipoDeporte.FUTBOL, NivelJuego.INTERMEDIO),
            crearUsuarioPrueba("maria.garcia", "maria@ejemplo.com", "password123", 
                TipoDeporte.BASQUET, NivelJuego.AVANZADO),
            crearUsuarioPrueba("carlos.lopez", "carlos@ejemplo.com", "password123", 
                TipoDeporte.TENIS, NivelJuego.PRINCIPIANTE),
            crearUsuarioPrueba("ana.martinez", "ana@ejemplo.com", "password123", 
                TipoDeporte.VOLEY, NivelJuego.INTERMEDIO),
            crearUsuarioPrueba("pedro.rodriguez", "pedro@ejemplo.com", "password123", 
                TipoDeporte.FUTBOL, NivelJuego.AVANZADO)
        );

        usuarioRepository.saveAll(usuariosPrueba);
        System.out.println("✅ Usuarios de prueba creados: " + usuariosPrueba.size());
    }

    // Métodos auxiliares
    private Ubicacion crearUbicacion(String direccion, Double latitud, Double longitud, String zona) {
        return Ubicacion.builder()
                .direccion(direccion)
                .latitud(latitud)
                .longitud(longitud)
                .zona(zona)
                .build();
    }

    private Ubicacion crearUbicacionSinCoordenadas(String direccion, String zona) {
        return Ubicacion.builder()
                .direccion(direccion)
                .latitud(null) // Sin coordenadas
                .longitud(null) // Sin coordenadas
                .zona(zona)
                .build();
    }

    private Usuario crearUsuarioPrueba(String nombreUsuario, String email, String password, 
                                     TipoDeporte deporteFavorito, NivelJuego nivelJuego) {
        return Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .email(email)
                .contrasena(passwordEncoder.encode(password))
                .deporteFavorito(deporteFavorito)
                .nivelJuego(nivelJuego)
                .role(Role.JUGADOR)
                .activo(true)
                .build();
    }
}