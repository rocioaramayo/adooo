package com.uade.tpo.deportes.entity;

import com.uade.tpo.deportes.enums.NivelJuego;
import com.uade.tpo.deportes.enums.Role;
import com.uade.tpo.deportes.enums.TipoDeporte;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private TipoDeporte deporteFavorito;

    @Enumerated(EnumType.STRING)
    private NivelJuego nivelJuego;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Implementación UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role.name());
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
