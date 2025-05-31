package com.ap.enlatados.controller;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.RepartidorDTO;
import com.ap.enlatados.entity.Repartidor;
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

    @PostMapping(
    	      path = "/cargar-csv",
    	      produces = MediaType.TEXT_PLAIN_VALUE
    	    )
    	    public ResponseEntity<String> cargarCsv(@RequestParam("archivo") MultipartFile archivo) {
    	        try {
    	            int inserted = svc.cargarRepartidoresDesdeCsv(archivo.getInputStream());
    	            return ResponseEntity
    	                .status(HttpStatus.CREATED)
    	                .body("Repartidores cargados: " + inserted);
    	        }
    	        catch (RepartidorService.BulkLoadException ex) {
    	            return ResponseEntity
    	                .status(HttpStatus.CONFLICT)
    	                .body("Error de duplicados: " + ex.getMessage());
    	        }
    	        catch (IOException e) {
    	            return ResponseEntity
    	                .status(HttpStatus.BAD_REQUEST)
    	                .body("Error leyendo CSV: " + e.getMessage());
           }
        }
    

    @GetMapping(path = "/diagrama-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiagramDTO> diagramaJson() {
        DiagramDTO dto = svc.obtenerDiagramaRepartidoresDTO();
        return ResponseEntity.ok(dto);
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