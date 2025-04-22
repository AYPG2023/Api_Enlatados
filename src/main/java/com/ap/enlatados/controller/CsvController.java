package com.ap.enlatados.controller;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.service.CsvLoaderService;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins="*")
public class CsvController {

    private final CsvLoaderService svc;

    public CsvController(CsvLoaderService svc) {
        this.svc = svc;
    }

    @PostMapping("/cargar")
    public ResponseEntity<?> cargarTodo() {
        try {
            svc.cargarUsuarios(Paths.get("src/main/resources/data/usuarios.csv"));
            svc.cargarClientes(Paths.get("src/main/resources/data/clientes.csv"));
            svc.cargarRepartidores(Paths.get("src/main/resources/data/repartidores.csv"));
            svc.cargarVehiculos(Paths.get("src/main/resources/data/vehiculos.csv"));
            return ResponseEntity.ok("CSV cargados correctamente");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al leer CSV: "+ex.getMessage());
        }
    }
}
