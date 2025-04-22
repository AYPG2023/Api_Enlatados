package com.ap.enlatados.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.ap.enlatados.model.Pedido;
import com.ap.enlatados.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins="*")
public class PedidoController {

    private final PedidoService svc;

    public PedidoController(PedidoService svc) {
        this.svc = svc;
    }

    public static class CrearPedidoDTO {
        public String origen;
        public String destino;
        public Long clienteId;
        public int numCajas;
    }

    public static class ActualizarPedidoDTO {
        public String origen;
        public String destino;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearPedidoDTO dto) {
        try {
            Pedido p = svc.crear(dto.origen, dto.destino, dto.clienteId, dto.numCajas);
            return ResponseEntity.status(HttpStatus.CREATED).body(p);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    public List<Pedido> listar() {
        return svc.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Pedido p = svc.buscar(id);
        if (p == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
        }
        return ResponseEntity.ok(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ActualizarPedidoDTO dto) {
        try {
            Pedido upd = svc.actualizar(id, dto.origen, dto.destino);
            return ResponseEntity.ok(upd);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            svc.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<?> completar(@PathVariable Long id) {
        try {
            Pedido p = svc.completar(id);
            return ResponseEntity.ok(p);
        } catch (Exception ex) {
            HttpStatus status = ex instanceof IllegalStateException
                                ? HttpStatus.BAD_REQUEST
                                : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(ex.getMessage());
        }
    }
}