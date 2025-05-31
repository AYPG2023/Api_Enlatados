package com.ap.enlatados.controller;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.VehiculoDTO;
import com.ap.enlatados.entity.Vehiculo;
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

    /**
     * Crear un vehículo y encolarlo en DISPONIBLES.
     */
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
            dto.getTransmision(),
            dto.getTipoVehiculo()
        );
        svc.crear(v);
        return ResponseEntity.status(HttpStatus.CREATED).body(v);
    }

    /**
     * Listar vehículos DISPONIBLES.
     * Se pueden filtrar por tipoVehiculo o por tipoLicencia.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Vehiculo> listar(
        @RequestParam(required = false) String tipoVehiculo,
        @RequestParam(required = false) String tipoLicencia
    ) {
        if (tipoLicencia != null) {
            return svc.listarPorLicencia(tipoLicencia);
        }
        if (tipoVehiculo != null) {
            return svc.listarPorTipo(tipoVehiculo);
        }
        return svc.listar();
    }

    /** Obtener un vehículo disponible por placa */
    @GetMapping("/{placa}")
    public ResponseEntity<Vehiculo> buscar(@PathVariable String placa) {
        return ResponseEntity.ok(svc.buscar(placa));
    }

    /** Actualizar datos de un vehículo (se reemplaza en la cola) */
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
            dto.getTransmision(),
            dto.getTipoVehiculo()
        );
        svc.modificar(placa, nuevo);
        return ResponseEntity.ok(nuevo);
    }

    /** Eliminar un vehículo disponible por placa */
    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> eliminar(@PathVariable String placa) {
        svc.eliminar(placa);
        return ResponseEntity.noContent().build();
    }


    /**
     * Carga masiva desde CSV con cabecera:
     * Placa;Marca;Modelo;Color;año;Tipo de transmisión;TipoVehiculo
     */
    @PostMapping(
      path = "/cargar-csv",
      produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> cargarCsv(@RequestParam("archivo") MultipartFile archivo) {
        try {
            int cargados = svc.cargarMasivo(archivo.getInputStream());
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Vehículos cargados: " + cargados);
        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error al procesar CSV: " + e.getMessage());
        }
    }
    /**
     * Asigna (dequeue) el siguiente vehículo disponible.
     */
    @PostMapping(path = "/asignar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> asignarSiguiente() {
        Vehiculo v = svc.dequeue();
        if (v == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No hay vehículos disponibles");
        }
        return ResponseEntity.ok(v);
    }

    /**
     * Libera (reenqueue) un vehículo al terminar el pedido.
     */
    @PostMapping("/reenqueue")
    public ResponseEntity<String> reenqueue(@RequestParam String placa) {
        Vehiculo v = svc.buscar(placa);
        svc.reenqueue(v);
        return ResponseEntity.ok("Vehículo reencolado: " + placa);
    }

    /**
     * Diagrama JSON de la cola de vehículos (nodos y aristas).
     */
    @GetMapping(path = "/diagrama-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiagramDTO> diagramaJson() {
        DiagramDTO dto = svc.obtenerDiagramaColaDTO();
        return ResponseEntity.ok(dto);
    }
}
