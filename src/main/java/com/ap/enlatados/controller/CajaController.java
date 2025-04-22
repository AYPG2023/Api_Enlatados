package com.ap.enlatados.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.model.Caja;
import com.ap.enlatados.service.CajaService;

@RestController
@RequestMapping("/api/cajas")
@CrossOrigin(origins="*")
public class CajaController {

    private final CajaService svc;

    public CajaController(CajaService svc) {
        this.svc = svc;
    }

    /** Crear (push) una nueva caja **/
    @PostMapping
    public ResponseEntity<Caja> crear() {
        Caja c = svc.crear();
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    /** Listar todas las cajas **/
    @GetMapping
    public List<Caja> listar() {
        return svc.listar();
    }

    /** Extraer (pop) la caja superior **/
    @DeleteMapping("/extraer")
    public ResponseEntity<?> extraer() {
        Caja c = svc.extraer();
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay cajas disponibles");
        }
        return ResponseEntity.ok(c);
    }
}