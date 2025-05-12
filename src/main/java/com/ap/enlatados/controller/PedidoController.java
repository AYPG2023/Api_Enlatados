package com.ap.enlatados.controller;

import com.ap.enlatados.model.CajaPedido;
import com.ap.enlatados.model.Pedido;
import com.ap.enlatados.service.ClienteService;
import com.ap.enlatados.service.PedidoService;
import com.ap.enlatados.service.RepartidorService;
import com.ap.enlatados.service.VehiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final RepartidorService repartidorService;
    private final VehiculoService vehiculoService;

    public PedidoController(PedidoService pedidoService, ClienteService clienteService, RepartidorService repartidorService, VehiculoService vehiculoService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.repartidorService = repartidorService;
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestParam String deptoOrigen,
                                         @RequestParam String deptoDestino,
                                         @RequestParam String dpiCliente) {
        Pedido p = pedidoService.crearPedido(deptoOrigen, deptoDestino,
                clienteService.buscar(dpiCliente),
                repartidorService.dequeue(),
                vehiculoService.dequeue());
        return ResponseEntity.ok(p);
    }

    @PostMapping("/agregar-caja")
    public ResponseEntity<?> agregarCaja(@RequestParam long numeroPedido,
                                         @RequestParam long idCaja,
                                         @RequestParam String fechaIngreso) {
        pedidoService.agregarCajaAlPedido(numeroPedido, new CajaPedido(idCaja, fechaIngreso));
        return ResponseEntity.ok("Caja agregada al pedido");
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @GetMapping("/diagrama")
    public ResponseEntity<String> obtenerDiagrama() {
        return ResponseEntity.ok(pedidoService.obtenerDiagramaPedidos());
    }

    @PostMapping("/cargar-csv")
    public ResponseEntity<?> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> registros = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(linea.split(";"));
            }
            pedidoService.cargarDesdeCsv(registros, clienteService, repartidorService, vehiculoService);
            return ResponseEntity.ok("Pedidos cargados");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cargar CSV: " + e.getMessage());
        }
    }
}
