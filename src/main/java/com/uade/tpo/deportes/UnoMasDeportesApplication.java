package com.uade.tpo.deportes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UnoMasDeportesApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnoMasDeportesApplication.class, args);
        System.out.println("🏃‍♂️ UnoMas - Sistema de Gestión Deportiva iniciado correctamente");
        System.out.println("📊 Panel de administración: http://localhost:8080");
        System.out.println("🔐 Usuario admin: admin@unomas.com / admin123");
    }
}