// src/main/java/com/ap/enlatados/controller/PedidoController.java
package com.ap.enlatados.controller;

import com.ap.enlatados.dto.CajaPedidoDTO;
import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.model.CajaPedido;
import com.ap.enlatados.model.Pedido;
import com.ap.enlatados.service.ClienteService;
import com.ap.enlatados.service.PedidoService;
import com.ap.enlatados.service.RepartidorService;
import com.ap.enlatados.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final RepartidorService repartidorService;
    private final VehiculoService vehiculoService;

    public PedidoController(PedidoService pedidoService,
                            ClienteService clienteService,
                            RepartidorService repartidorService,
                            VehiculoService vehiculoService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.repartidorService = repartidorService;
        this.vehiculoService = vehiculoService;
    }

    /** Crear pedido recibiendo JSON con deptos y DPI de cliente */
    @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Pedido> crearPedido(@RequestBody @Valid PedidoDTO dto) {
        Pedido p = pedidoService.crearPedido(
            dto.getDeptoOrigen(),
            dto.getDeptoDestino(),
            clienteService.buscar(dto.getDpiCliente()),
            repartidorService.dequeue(),
            vehiculoService.dequeue()
        );
        return ResponseEntity.ok(p);
    }

    /** Agregar caja a un pedido existente via JSON */
    @PostMapping(
      path = "/agregar-caja",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> agregarCaja(@RequestBody @Valid CajaPedidoDTO dto) {
        pedidoService.agregarCajaAlPedido(
            dto.getNumeroPedido(),
            new CajaPedido(dto.getIdCaja(), dto.getFechaIngreso())
        );
        return ResponseEntity.ok("Caja agregada al pedido " + dto.getNumeroPedido());
    }

    /** Listar todos los pedidos */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    /** Diagrama textual de pedidos */
    @GetMapping(
      path = "/diagrama",
      produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String obtenerDiagrama() {
        return pedidoService.obtenerDiagramaPedidos();
    }

    /** Carga masiva desde CSV (DeptoOrigen;DeptoDestino;DPI Cliente) */
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
            pedidoService.cargarDesdeCsv(
                registros,
                clienteService,
                repartidorService,
                vehiculoService
            );
            return ResponseEntity.ok("Pedidos cargados desde CSV");
        } catch (IOException e) {
            return ResponseEntity
                .badRequest()
                .body("Error al procesar CSV: " + e.getMessage());
        }
    }
}
