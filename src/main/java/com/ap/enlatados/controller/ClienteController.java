package com.ap.enlatados.controller;

import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestParam String dpi,
                                   @RequestParam String nombre,
                                   @RequestParam String apellidos,
                                   @RequestParam String telefono) {
        clienteService.crear(new Cliente(dpi, nombre, apellidos, telefono));
        return ResponseEntity.ok("Cliente creado");
    }

    @GetMapping("/{dpi}")
    public ResponseEntity<?> buscar(@PathVariable String dpi) {
        return ResponseEntity.ok(clienteService.buscar(dpi));
    }

    @DeleteMapping("/{dpi}")
    public ResponseEntity<?> eliminar(@PathVariable String dpi) {
        clienteService.eliminar(dpi);
        return ResponseEntity.ok("Cliente eliminado");
    }

    @PutMapping("/{dpi}")
    public ResponseEntity<?> actualizar(@PathVariable String dpi,
                                        @RequestParam String nombre,
                                        @RequestParam String apellidos,
                                        @RequestParam String telefono) {
        clienteService.actualizar(dpi, new Cliente(dpi, nombre, apellidos, telefono));
        return ResponseEntity.ok("Cliente actualizado");
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @PostMapping("/cargar-csv")
    public ResponseEntity<?> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> registros = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(linea.split(";"));
            }
            clienteService.cargarMasivo(registros);
            return ResponseEntity.ok("Clientes cargados");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cargar CSV: " + e.getMessage());
        }
    }

    @GetMapping("/diagrama")
    public ResponseEntity<String> obtenerDiagrama() {
        return ResponseEntity.ok(clienteService.obtenerDiagramaAVL());
    }
}
