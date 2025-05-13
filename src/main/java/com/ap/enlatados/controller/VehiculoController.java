package com.ap.enlatados.controller;

import com.ap.enlatados.model.Vehiculo;
import com.ap.enlatados.service.VehiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    private final VehiculoService svc;

    public VehiculoController(VehiculoService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Vehiculo vehiculo) {
        svc.crear(vehiculo);
        return ResponseEntity.ok("Vehículo creado");
    }

    @PutMapping("/{placa}")
    public ResponseEntity<?> actualizar(@PathVariable String placa, @RequestBody Vehiculo vehiculo) {
        svc.modificar(placa, vehiculo);
        return ResponseEntity.ok("Vehículo actualizado");
    }

    @GetMapping("/{placa}")
    public ResponseEntity<?> buscar(@PathVariable String placa) {
        return ResponseEntity.ok(svc.buscar(placa));
    }

    @DeleteMapping("/{placa}")
    public ResponseEntity<?> eliminar(@PathVariable String placa) {
        svc.eliminar(placa);
        return ResponseEntity.ok("Vehículo eliminado");
    }

    @GetMapping
    public List<Vehiculo> listar() {
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
            return ResponseEntity.ok("Vehículos cargados");
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
        Vehiculo v = svc.dequeue();
        if (v == null) return ResponseEntity.status(404).body("No hay vehículos disponibles");
        return ResponseEntity.ok(v);
    }

    @PostMapping("/reenqueue")
    public ResponseEntity<?> reenqueue(@RequestParam String placa) {
        Vehiculo v = svc.buscar(placa);
        svc.reenqueue(v);
        return ResponseEntity.ok("Vehículo reencolado");
    }
}
