package com.ap.enlatados.service;

import java.io.IOException;
import java.nio.file.*;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final CsvLoaderService csvLoader;

    public DataInitializer(CsvLoaderService csvLoader) {
        this.csvLoader = csvLoader;
    }

    @PostConstruct
    public void init() {
        // Rutas a CSV en resources/data
        Path dir = Paths.get("src/main/resources/data");
        try {
            if (Files.exists(dir.resolve("usuarios.csv"))) {
                csvLoader.cargarUsuarios(dir.resolve("usuarios.csv"));
            }
            if (Files.exists(dir.resolve("clientes.csv"))) {
                csvLoader.cargarClientes(dir.resolve("clientes.csv"));
            }
            if (Files.exists(dir.resolve("repartidores.csv"))) {
                csvLoader.cargarRepartidores(dir.resolve("repartidores.csv"));
            }
            if (Files.exists(dir.resolve("vehiculos.csv"))) {
                csvLoader.cargarVehiculos(dir.resolve("vehiculos.csv"));
            }
            System.out.println("== Carga CSV (opcional) completada ==");
        } catch (IOException e) {
            // Solo lo registramos, no detenemos el arranque
            System.err.println("No se pudieron cargar todos los CSV: " + e.getMessage());
        }
    }
}
