package com.ap.enlatados.controller;

import com.ap.enlatados.dto.RepartidorDTO;
import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.service.RepartidorService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/repartidores")
@CrossOrigin(origins = "*")
public class RepartidorController {

    private final RepartidorService svc;

    public RepartidorController(RepartidorService svc) {
        this.svc = svc;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Repartidor> crear(@RequestBody @Valid RepartidorDTO dto) {
        Repartidor r = new Repartidor(
            dto.getDpi(),
            dto.getNombre(),
            dto.getApellidos(),
            dto.getTipoLicencia(),
            dto.getNumeroLicencia(),
            dto.getTelefono()
        );
        svc.crear(r);
        return ResponseEntity.status(HttpStatus.CREATED).body(r);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Repartidor> listar() {
        return svc.listar();
    }

    @GetMapping("/{dpi}")
    public ResponseEntity<Repartidor> buscar(@PathVariable String dpi) {
        return ResponseEntity.ok(svc.buscar(dpi));
    }

    @PutMapping(path = "/{dpi}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Repartidor> actualizar(
        @PathVariable String dpi,
        @RequestBody @Valid RepartidorDTO dto
    ) {
        Repartidor nuevo = new Repartidor(
            dto.getDpi(),
            dto.getNombre(),
            dto.getApellidos(),
            dto.getTipoLicencia(),
            dto.getNumeroLicencia(),
            dto.getTelefono()
        );
        svc.modificar(dpi, nuevo);
        return ResponseEntity.ok(nuevo);
    }

    @DeleteMapping("/{dpi}")
    public ResponseEntity<Void> eliminar(@PathVariable String dpi) {
        svc.eliminar(dpi);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/cargar-csv",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> cargarCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))
        ) {
            List<String[]> regs = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                regs.add(linea.split(";"));
            }
            svc.cargarMasivo(regs);
            return ResponseEntity.ok("Repartidores cargados desde CSV");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Error al procesar CSV: " + e.getMessage());
        }
    }

    @GetMapping(path = "/diagrama", produces = MediaType.TEXT_PLAIN_VALUE)
    public String diagrama() {
        return svc.obtenerDiagramaCola();
    }

    @PostMapping(path = "/asignar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> asignarSiguiente() {
        Repartidor r = svc.dequeue();
        if (r == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No hay repartidores disponibles");
        return ResponseEntity.ok(r);
    }

    @PostMapping(path = "/reenqueue")
    public ResponseEntity<String> reenqueue(@RequestParam String dpi) {
        Repartidor r = svc.buscar(dpi);
        svc.reenqueue(r);
        return ResponseEntity.ok("Repartidor reencolado");
    }
}