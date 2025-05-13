package com.ap.enlatados.controller;

import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.service.RepartidorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
@CrossOrigin(origins = "*")
public class RepartidorController {

    private final RepartidorService svc;

    public RepartidorController(RepartidorService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Repartidor repartidor) {
        svc.crear(repartidor);
        return ResponseEntity.ok("Repartidor creado");
    }

    @PutMapping("/{dpi}")
    public ResponseEntity<?> actualizar(@PathVariable String dpi, @RequestBody Repartidor repartidor) {
        svc.modificar(dpi, repartidor);
        return ResponseEntity.ok("Repartidor actualizado");
    }

    @GetMapping("/{dpi}")
    public ResponseEntity<?> buscar(@PathVariable String dpi) {
        return ResponseEntity.ok(svc.buscar(dpi));
    }

    @DeleteMapping("/{dpi}")
    public ResponseEntity<?> eliminar(@PathVariable String dpi) {
        svc.eliminar(dpi);
        return ResponseEntity.ok("Repartidor eliminado");
    }

    @GetMapping
    public List<Repartidor> listar() {
        return svc.listar();
    }

    @PostMapping("/cargar-csv")
    public ResponseEntity<?> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> registros = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(linea.split(";"));
            }
            svc.cargarMasivo(registros);
            return ResponseEntity.ok("Repartidores cargados");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cargar CSV: " + e.getMessage());
        }
    }

    @GetMapping("/diagrama")
    public ResponseEntity<String> obtenerDiagrama() {
        return ResponseEntity.ok(svc.obtenerDiagramaCola());
    }

    @GetMapping("/asignar")
    public ResponseEntity<?> asignarSiguiente() {
        Repartidor r = svc.dequeue();
        if (r == null) return ResponseEntity.status(404).body("No hay repartidores disponibles");
        return ResponseEntity.ok(r);
    }

    @PostMapping("/reenqueue")
    public ResponseEntity<?> reenqueue(@RequestParam String dpi) {
        Repartidor r = svc.buscar(dpi);
        svc.reenqueue(r);
        return ResponseEntity.ok("Repartidor reencolado");
    }
}
