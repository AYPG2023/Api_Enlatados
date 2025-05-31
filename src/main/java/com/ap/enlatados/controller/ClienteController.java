package com.ap.enlatados.controller;

import com.ap.enlatados.dto.ClienteDTO;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.entity.Cliente;
import com.ap.enlatados.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /** Crear cliente recibiendo JSON */
    @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Cliente> crear(@RequestBody @Valid ClienteDTO dto) {
        Cliente c = new Cliente(
            dto.getDpi(),
            dto.getNombre(),
            dto.getApellidos(),
            dto.getTelefono(),
            dto.getDireccion()
        );
        clienteService.crear(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    /**
     * /api/clientes/{dpi}:
     * Busca un cliente por su DPI y, si existe, lo devuelve.
     */
    @GetMapping(path = "/{dpi}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cliente> obtenerPorDpi(@PathVariable String dpi) {
        try {
            Cliente c = clienteService.buscar(dpi);
            return ResponseEntity.ok(c);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    /** Actualizar cliente por DPI recibiendo JSON */
    @PutMapping(
      path = "/{dpi}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Cliente> actualizar(
        @PathVariable String dpi,
        @RequestBody @Valid ClienteDTO dto
    ) {
        Cliente actualizado = new Cliente(
            dto.getDpi(),
            dto.getNombre(),
            dto.getApellidos(),
            dto.getTelefono(),
            dto.getDireccion()
        );
        clienteService.actualizar(dpi, actualizado);
        return ResponseEntity.ok(actualizado);
    }

    /** Eliminar cliente por DPI */
    @DeleteMapping("/{dpi}")
    public ResponseEntity<String> eliminar(@PathVariable String dpi) {
        clienteService.eliminar(dpi);
        return ResponseEntity.ok("Cliente eliminado");
    }

    /** Listar todos los clientes */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    /**
     * Carga masiva desde CSV (cada línea: dpi;nombre;apellidos;telefono;direccion)
     * Retorna texto plano con total cargado.
     */

    @PostMapping(
      path = "/cargar-csv",
      produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try {
            int count = clienteService.cargarClientesDesdeCsv(archivo.getInputStream());
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Clientes cargados: " + count);
        }
        catch (ClienteService.BulkLoadException ex) {
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
    
    /** Devuelve diagrama textual del AVL en texto plano */
    @GetMapping(path = "/diagrama-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiagramDTO> diagramaJson() {
        DiagramDTO dto = clienteService.obtenerDiagramaClientesDTO();
        return ResponseEntity.ok(dto);
    }
}
