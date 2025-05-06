package com.ap.enlatados.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.dto.UsuarioNode;
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
        Optional<Usuario> opt = svc.buscar(id);
        return opt
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                                           .body("Usuario no encontrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody Usuario u) {
        try {
            Usuario updated = svc.actualizar(id, u);
            return ResponseEntity.ok(updated);
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

    /** Devuelve la lista enlazada interna de usuarios como DTO */
    @GetMapping("/linked")
    public ResponseEntity<UsuarioNode> linkedList() {
        UsuarioNode head = svc.obtenerListaEnlazada();
        return ResponseEntity.ok(head);
    }
}
