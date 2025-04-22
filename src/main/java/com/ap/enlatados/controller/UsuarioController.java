package com.ap.enlatados.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.model.Usuario;
import com.ap.enlatados.service.UsuarioService;


@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins="*")
public class UsuarioController {

    private final UsuarioService svc;

    public UsuarioController(UsuarioService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario u) {
        Usuario c = svc.crear(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    @GetMapping
    public List<Usuario> listar() {
        return svc.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        // Busca el usuario
        Optional<Usuario> opt = svc.buscar(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario u) {
        try {
            Usuario updated = svc.actualizar(id, u);
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
