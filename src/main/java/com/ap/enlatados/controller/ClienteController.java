package com.ap.enlatados.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins="*")
public class ClienteController {

    private final ClienteService svc;

    public ClienteController(ClienteService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente c) {
        Cliente saved = svc.crear(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Cliente> listar() {
        return svc.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Cliente c = svc.buscarPorId(id);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }
        return ResponseEntity.ok(c);
    }

    @GetMapping("/dpi/{dpi}")
    public ResponseEntity<?> obtenerPorDpi(@PathVariable String dpi) {
        Cliente c = svc.buscarPorDpi(dpi);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }
        return ResponseEntity.ok(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Cliente c) {
        try {
            Cliente updated = svc.actualizar(id, c);
            return ResponseEntity.ok(updated);
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
}