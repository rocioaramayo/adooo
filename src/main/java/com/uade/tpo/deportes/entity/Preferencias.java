package com.uade.tpo.deportes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preferencias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
        name = "preferencias_deportes",
        joinColumns = @JoinColumn(name = "preferencias_id"),
        inverseJoinColumns = @JoinColumn(name = "deporte_id")
    )
    private List<Deporte> deportesFavoritos;

    @Column
    private Integer radioBusqueda; // en kilómetros

    @Column
    private Boolean notificacionesEmail;

    @Column
    private Boolean notificacionesPush;

    @Column
    private String zonaPreferida; // barrio o zona preferida para jugar
} 