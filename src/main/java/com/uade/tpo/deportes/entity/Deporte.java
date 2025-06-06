package com.uade.tpo.deportes.entity;

import com.uade.tpo.deportes.enums.TipoDeporte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "deportes")
public class Deporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeporte tipo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer jugadoresPorEquipo;

    @Column(columnDefinition = "TEXT")
    private String reglasBasicas;

    public boolean validarConfiguracion() {
        return nombre != null && 
               !nombre.trim().isEmpty() && 
               jugadoresPorEquipo != null && 
               jugadoresPorEquipo > 0;
    }
}
