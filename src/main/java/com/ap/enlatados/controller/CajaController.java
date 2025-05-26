package com.ap.enlatados.controller;

import com.ap.enlatados.service.CajaService;
import com.ap.enlatados.dto.CajaDTO;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.ResumenDTO;
import com.ap.enlatados.entity.Caja;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cajas")
@CrossOrigin(origins = "*")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    /**
     * Push: agrega N cajas para el producto dado.
     * POST /api/cajas
     * Body: { "producto": "...", "cantidad": 5 }
     */
    @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Caja>> push(
        @RequestBody @Valid CajaDTO dto
    ) {
        List<Caja> creadas = cajaService.agregarCajas(dto.getProducto(), dto.getCantidad());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creadas);
    }

    /**
     * Pop: extrae N cajas del tope de la pila del producto.
     * POST /api/cajas/extraer
     * Body: { "producto": "...", "cantidad": 3 }
     */
    @PostMapping(
      path = "/extraer",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> pop(
        @RequestBody @Valid CajaDTO dto
    ) {
        List<Caja> sacadas = cajaService.extraerCajas(dto.getProducto(), dto.getCantidad());
        if (sacadas.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No hay suficientes cajas de " + dto.getProducto());
        }
        return ResponseEntity.ok(sacadas);
    }

    /**
     * Listar todas las cajas de un producto en orden LIFO (tope primero).
     * GET /api/cajas?producto=...
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Caja>> listar(
        @RequestParam String producto
    ) {
        List<Caja> todas = cajaService.listarCajas(producto);
        return ResponseEntity.ok(todas);
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<ResumenDTO>> resumen() {
        return ResponseEntity.ok(cajaService.obtenerResumenDeProductos());
    }

    /**
     * Carga masiva desde CSV. Cada línea solo lleva el ID:
     * POST /api/cajas/cargar-csv?producto=...
     * Form-data: archivo (CSV)
     */
    @PostMapping(
      path = "/cargar-csv",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> cargarDesdeCsv(
        @RequestParam String producto,
        @RequestParam("archivo") MultipartFile archivo
    ) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))
        ) {
            List<String[]> registros = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(new String[]{ linea.trim() });
            }
            int total = cajaService.cargarDesdeCsv(producto, registros);
            return ResponseEntity.ok("Se cargaron " + total + " cajas de " + producto);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    /**
     * Devuelve un diagrama textual de la pila del producto:
     * GET /api/cajas/diagrama?producto=...
     */
    @GetMapping(path = "/diagrama-general-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiagramDTO> diagramaGeneralJson() {
        DiagramDTO dto = cajaService.obtenerDiagramaProductosDTO();
        return ResponseEntity.ok(dto);
    }
}
