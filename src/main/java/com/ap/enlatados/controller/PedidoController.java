package com.ap.enlatados.controller;

import com.ap.enlatados.dto.CajaPedidoDTO;
import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.dto.AsignarPedidoDTO;
import com.ap.enlatados.model.CajaPedido;
import com.ap.enlatados.model.Pedido;
import com.ap.enlatados.service.ClienteService;
import com.ap.enlatados.service.PedidoService;
import com.ap.enlatados.service.RepartidorService;
import com.ap.enlatados.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
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

    public PedidoController(
      PedidoService pedidoService,
      ClienteService clienteService,
      RepartidorService repartidorService,
      VehiculoService vehiculoService
    ) {
        this.pedidoService     = pedidoService;
        this.clienteService    = clienteService;
        this.repartidorService = repartidorService;
        this.vehiculoService   = vehiculoService;
    }

    /**
     * Crear un pedido. Extrae un repartidor y un vehículo automáticamente.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pedido> crearPedido(@RequestBody @Valid PedidoDTO dto) {
        Pedido p = pedidoService.crearPedido(
          dto.getDeptoOrigen(),
          dto.getDeptoDestino(),
          clienteService.buscar(dto.getDpiCliente()),
          repartidorService.dequeue(),
          vehiculoService.dequeue()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    /**
     * Listar pedidos (filtro opcional por estado: Pendiente, EnCurso, Completado)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Pedido>> listarPedidos(
      @RequestParam(required = false) String estado
    ) {
        return ResponseEntity.ok(
          pedidoService.listarPedidosPorEstado(estado)
        );
    }

    /**
     * Obtener detalle de un pedido por su número.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable("id") long id) {
        Pedido p = pedidoService.buscarPedido(id);
        return p != null
          ? ResponseEntity.ok(p)
          : ResponseEntity.notFound().build();
    }

    /**
     * Agregar una caja extra a un pedido existente.
     */
    @PostMapping(path = "/agregar-caja",
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> agregarCaja(
      @RequestBody @Valid CajaPedidoDTO dto
    ) {
        pedidoService.agregarCajaAlPedido(
          dto.getNumeroPedido(),
          new CajaPedido(dto.getIdCaja(), dto.getFechaIngreso())
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para asignar manualmente repartidor y vehículo.
     */
    @PatchMapping(path = "/{id}/asignar",
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pedido> asignarRecursos(
      @PathVariable("id") long id,
      @RequestBody @Valid AsignarPedidoDTO dto
    ) {
        Pedido actualizado = pedidoService.asignarRecursos(
          id,
          dto.getRepartidorDpi(),
          dto.getVehiculoPlaca()
        );
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Marcar pedido como completado y reencolar repartidor y vehículo.
     */
    @PutMapping("/{id}/completar")
    public ResponseEntity<Void> completarPedido(@PathVariable("id") long id) {
        boolean ok = pedidoService.completarPedido(
            id,
            repartidorService,
            vehiculoService
        );
        return ok
          ? ResponseEntity.ok().build()
          : ResponseEntity.notFound().build();
    }

    /**
     * Diagrama textual de pedidos en memoria para debug.
     */
    @GetMapping(path = "/diagrama", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> diagrama() {
        return ResponseEntity.ok(
          pedidoService.obtenerDiagramaPedidos()
        );
    }

    /**
     * Carga masiva de pedidos desde CSV.
     * Cada línea: Origen;Destino;DPICliente
     */
    @PostMapping(path = "/cargar-csv",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cargarDesdeCsv(
      @RequestParam("archivo") MultipartFile archivo
    ) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))
        ) {
            List<String[]> lineas = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                lineas.add(linea.split(";"));
            }
            pedidoService.cargarDesdeCsv(
              lineas,
              clienteService,
              repartidorService,
              vehiculoService
            );
            return ResponseEntity.ok("Pedidos cargados desde CSV: " + lineas.size());
        } catch (Exception e) {
            return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body("Error procesando CSV: " + e.getMessage());
        }
    }
}
