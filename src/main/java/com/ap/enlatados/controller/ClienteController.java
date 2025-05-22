// src/main/java/com/ap/enlatados/controller/ClienteController.java
package com.ap.enlatados.controller;

import com.ap.enlatados.dto.ClienteDTO;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.model.Cliente;
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
    public ResponseEntity<Void> eliminar(@PathVariable String dpi) {
        clienteService.eliminar(dpi);
        return ResponseEntity.noContent().build();
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
                registros.add(linea.split(";"));
            }
            clienteService.cargarMasivo(registros);
            return ResponseEntity.ok("Clientes cargados desde CSV");
        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error al procesar CSV: " + e.getMessage());
        }
    }

    /** Devuelve diagrama textual del AVL en texto plano */
    @GetMapping(path = "/diagrama-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiagramDTO> diagramaJson() {
        DiagramDTO dto = clienteService.obtenerDiagramaClientesDTO();
        return ResponseEntity.ok(dto);
    }
}
