// src/main/java/com/ap/enlatados/controller/VehiculoController.java
package com.ap.enlatados.controller;

import com.ap.enlatados.dto.VehiculoDTO;
import com.ap.enlatados.model.Vehiculo;
import com.ap.enlatados.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    private final VehiculoService svc;

    public VehiculoController(VehiculoService svc) {
        this.svc = svc;
    }

    /** Crea un vehículo recibiendo JSON en el body */
    @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Vehiculo> crear(@RequestBody @Valid VehiculoDTO dto) {
        Vehiculo v = new Vehiculo(
            dto.getPlaca(),
            dto.getMarca(),
            dto.getModelo(),
            dto.getColor(),
            dto.getAnio(),
            dto.getTransmision()
        );
        svc.crear(v);
        return ResponseEntity.status(HttpStatus.CREATED).body(v);
    }

    /** Devuelve un vehículo por placa */
    @GetMapping(value = "/{placa}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vehiculo> buscar(@PathVariable String placa) {
        return ResponseEntity.ok(svc.buscar(placa));
    }

    /** Actualiza un vehículo completo via JSON */
    @PutMapping(
      path = "/{placa}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Vehiculo> actualizar(
        @PathVariable String placa,
        @RequestBody @Valid VehiculoDTO dto
    ) {
        Vehiculo nuevo = new Vehiculo(
            placa,
            dto.getMarca(),
            dto.getModelo(),
            dto.getColor(),
            dto.getAnio(),
            dto.getTransmision()
        );
        svc.modificar(placa, nuevo);
        return ResponseEntity.ok(nuevo);
    }

    /** Elimina por placa */
    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> eliminar(@PathVariable String placa) {
        svc.eliminar(placa);
        return ResponseEntity.noContent().build();
    }

    /** Lista todos los vehículos */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Vehiculo> listar() {
        return svc.listar();
    }

    /** Carga masiva desde CSV */
    @PostMapping(
      path = "/cargar-csv",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))
        ) {
            List<String[]> registros = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(linea.split(";"));
            }
            svc.cargarMasivo(registros);
            return ResponseEntity.ok("Vehículos cargados desde CSV");
        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error al procesar CSV: " + e.getMessage());
        }
    }

    /** Diagrama textual de la cola */
    @GetMapping(value = "/diagrama", produces = MediaType.TEXT_PLAIN_VALUE)
    public String obtenerDiagrama() {
        return svc.obtenerDiagramaCola();
    }

    /** Extrae (dequeue) el siguiente vehículo */
    @PostMapping(value = "/asignar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> asignarSiguiente() {
        Vehiculo v = svc.dequeue();
        if (v == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No hay vehículos disponibles");
        }
        return ResponseEntity.ok(v);
    }

    /** Reencola un vehículo existente por placa */
    @PostMapping("/reenqueue")
    public ResponseEntity<String> reenqueue(@RequestParam String placa) {
        Vehiculo v = svc.buscar(placa);
        svc.reenqueue(v);
        return ResponseEntity.ok("Vehículo reencolado");
    }
}
