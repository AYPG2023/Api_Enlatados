package com.ap.enlatados.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.model.Vehiculo;
import com.ap.enlatados.service.VehiculoService;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins="*")
public class VehiculoController {

    private final VehiculoService svc;

    public VehiculoController(VehiculoService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Vehiculo> crear(@RequestBody Vehiculo v) {
        Vehiculo saved = svc.crear(v);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Vehiculo> listar() {
        return svc.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Vehiculo v = svc.buscar(id);
        if (v == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehículo no encontrado");
        }
        return ResponseEntity.ok(v);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Vehiculo v) {
        try {
            Vehiculo upd = svc.actualizar(id, v);
            return ResponseEntity.ok(upd);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            svc.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /** Asignar siguiente vehículo (dequeue) **/
    @GetMapping("/asignar")
    public ResponseEntity<?> asignarSiguiente() {
        Vehiculo v = svc.dequeue();
        if (v == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay vehículos disponibles");
        }
        return ResponseEntity.ok(v);
    }
}
