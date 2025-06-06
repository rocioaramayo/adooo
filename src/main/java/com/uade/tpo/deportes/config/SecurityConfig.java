package com.uade.tpo.deportes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.uade.tpo.deportes.enums.Role;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS y CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource)) 
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))

            // 2. Reglas de acceso - ADAPTADAS PARA EL DOMINIO DEPORTIVO
            .authorizeHttpRequests(auth -> auth

                // a) Público: auth, errores, deportes y ubicaciones (GET)
                .requestMatchers("/api/v1/auth/**", "/error/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/deportes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ubicaciones/**").permitAll()

                // b) Partidos: buscar es público, crear/modificar requiere autenticación
                .requestMatchers(HttpMethod.GET, "/api/v1/partidos/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/partidos/buscar").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/partidos").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/partidos/*/unirse").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/partidos/**").authenticated()

                // c) Usuarios: perfil y búsqueda solo autenticados
                .requestMatchers("/api/v1/usuarios/**").authenticated()

                // d) Notificaciones: solo usuarios autenticados
                .requestMatchers("/api/v1/notificaciones/**").authenticated()

                // e) Gestión administrativa: solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/deportes/crear/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers("/api/v1/admin/**").hasAuthority(Role.ADMIN.name())

                // f) Resto: autenticado
                .anyRequest().authenticated()
            )

            // 3. JWT filter
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}