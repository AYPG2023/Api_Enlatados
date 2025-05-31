package com.ap.enlatados.controller;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.entity.Pedido;
import com.ap.enlatados.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** 1) Crear pedido */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoDTO dto) {
   try {
       Pedido p = pedidoService.crearPedido(dto);

       // Si está pendiente, devolvemos un mensaje en texto
       if ("Pendiente".equalsIgnoreCase(p.getEstado())) {
           return ResponseEntity.status(HttpStatus.CREATED)
               .body("Pedido creado en estado PENDIENTE (ID: " 
                     + p.getNumeroPedido() 
                     + "). Asigne repartidor y vehículo más adelante cuando este disponible alguno"
                     + p.getNumeroPedido()
                     + "/asignar");
       }

       // Si se asignó todo, devolvemos el objeto Pedido
       return ResponseEntity.status(HttpStatus.CREATED).body(p);

   } catch (IllegalArgumentException ex) {
       return ResponseEntity
           .badRequest()
           .body(ex.getMessage());
   }
}


    /** 2) Listar pedidos */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Pedido>> listarPedidos(
      @RequestParam(required = false) String estado
    ) {
        return ResponseEntity.ok(
          pedidoService.listarPedidosPorEstado(estado)
        );
    }

    /** 3) Detalle */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable long id) {
        Pedido p = pedidoService.buscarPedido(id);
        return p != null
          ? ResponseEntity.ok(p)
          : ResponseEntity.notFound().build();
    }

    /** 4) Asignación automática de recursos */
    @PostMapping(path = "/{id}/asignar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pedido> asignarRecursosAuto(@PathVariable long id) {
        Pedido actualizado = pedidoService.asignarRecursosAutomatico(id);
        return ResponseEntity.ok(actualizado);
    }

    /** 5) Completar y reencolar */
    @PutMapping("/{id}/completar")
    public ResponseEntity<Void> completarPedido(@PathVariable long id) {
        boolean ok = pedidoService.completarPedido(id);
        return ok
          ? ResponseEntity.ok().build()
          : ResponseEntity.notFound().build();
    }

    /** 6) Cancelar (reencola recursos sin eliminar) */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarPedido(@PathVariable long id) {
        boolean ok = pedidoService.cancelarPedido(id);
        if (ok) {
            return ResponseEntity.ok("Pedido cancelado y recursos reencolados");
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("No se puede cancelar: comprueba que el pedido exista y esté en curso o pendiente");
        }
    }

    /** 7) Eliminar y reencolar solo completados o cancelados */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPedido(@PathVariable long id) {
        boolean ok = pedidoService.eliminarPedido(id);
        if (ok) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Solo se pueden eliminar pedidos completados o cancelados");
        }
    }

    /** 8) Diagrama (debug) */
    @GetMapping(path = "/diagrama-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiagramDTO> diagramaJson() {
        DiagramDTO dto = pedidoService.obtenerDiagramaPedidosDTO();
        return ResponseEntity.ok(dto);
    }

    /** 9) Carga CSV */
    @PostMapping(path = "/cargar-csv",
                 produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cargarDesdeCsv(
      @RequestParam("archivo") MultipartFile archivo
    ) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> lineas = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                lineas.add(linea.split(";"));
            }
            int count = pedidoService.cargarDesdeCsv(lineas);
            return ResponseEntity.ok("Pedidos cargados: " + count);
        } catch (Exception e) {
            return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body("Error procesando CSV: " + e.getMessage());
        }
    }
}
