package com.ap.enlatados.controller;

import com.ap.enlatados.model.Caja;
import com.ap.enlatados.service.CajaService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
     * Crea una nueva caja (push).
     * @return 201 + la caja creada en JSON.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Caja> agregarCaja() {
        Caja c = cajaService.agregarCaja();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(c);
    }

    /**
     * Extrae (pop) la caja superior.
     * @return 200 + la caja extraída, o 404 si no hay.
     */
    @PostMapping(value = "/extraer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> extraerCaja() {
        Caja c = cajaService.extraerCaja();
        if (c == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No hay cajas disponibles");
        }
        return ResponseEntity.ok(c);
    }

    /**
     * Lista todas las cajas en la pila.
     * @return JSON array de cajas.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Caja> listarCajas() {
        return cajaService.listarCajas();
    }

    /**
     * Carga masiva desde CSV. Formato: cada línea contiene solo el ID.
     * @return 200 + mensaje con total de cajas cargadas.
     */
    @PostMapping(
      path = "/cargar-csv",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))
        ) {
            List<String[]> registros = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(new String[]{ linea.trim() });
            }
            int total = cajaService.cargarDesdeCsv(registros);
            return ResponseEntity.ok("Se cargaron " + total + " cajas a la pila.");
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    /**
     * Devuelve un diagrama textual de la pila: "[idN] -> ... -> NULL"
     */
    @GetMapping(
      path = "/diagrama",
      produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String obtenerDiagrama() {
        return cajaService.obtenerDiagramaPila();
    }
}
