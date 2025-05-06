package com.ap.enlatados.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.dto.RepartidorNode;
import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.service.RepartidorService;

@RestController
@RequestMapping("/api/repartidores")
@CrossOrigin(origins="*")
public class RepartidorController {

    private final RepartidorService svc;

    public RepartidorController(RepartidorService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Repartidor> crear(@RequestBody Repartidor r) {
        Repartidor saved = svc.crear(r);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Repartidor> listar() {
        return svc.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Repartidor r = svc.buscar(id);
        if (r == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Repartidor no encontrado");
        }
        return ResponseEntity.ok(r);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody Repartidor r) {
        try {
            Repartidor upd = svc.actualizar(id, r);
            return ResponseEntity.ok(upd);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            svc.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(ex.getMessage());
        }
    }

    /** Extrae el siguiente repartidor (dequeue) **/
    @GetMapping("/asignar")
    public ResponseEntity<?> asignarSiguiente() {
        Repartidor r = svc.dequeue();
        if (r == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No hay repartidores disponibles");
        }
        return ResponseEntity.ok(r);
    }

    /** Devuelve la cola completa como lista enlazada de DTOs **/
    @GetMapping("/linked")
    public ResponseEntity<RepartidorNode> colaEnlazada() {
        RepartidorNode head = svc.obtenerColaEnlazada();
        return ResponseEntity.ok(head);
    }
}
