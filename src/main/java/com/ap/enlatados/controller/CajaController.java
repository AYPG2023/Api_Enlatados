package com.ap.enlatados.controller;

import com.ap.enlatados.model.Caja;
import com.ap.enlatados.service.CajaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cajas")
@CrossOrigin(origins = "*")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @PostMapping
    public ResponseEntity<Caja> agregarCaja() {
        return ResponseEntity.ok(cajaService.agregarCaja());
    }

    @DeleteMapping("/extraer")
    public ResponseEntity<?> extraerCaja() {
        Caja c = cajaService.extraerCaja();
        if (c == null) return ResponseEntity.status(404).body("No hay cajas disponibles");
        return ResponseEntity.ok(c);
    }

    @GetMapping
    public List<Caja> listarCajas() {
        return cajaService.listarCajas();
    }

    @PostMapping("/cargar-csv")
    public ResponseEntity<?> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> registros = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(linea.split(";"));
            }
            int total = cajaService.cargarDesdeCsv(registros);
            return ResponseEntity.ok("Se cargaron " + total + " cajas a la pila.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/diagrama")
    public ResponseEntity<String> obtenerDiagrama() {
        return ResponseEntity.ok(cajaService.obtenerDiagramaPila());
    }
}
