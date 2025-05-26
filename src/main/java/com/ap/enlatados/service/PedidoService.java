package com.ap.enlatados.service;

import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.dto.PedidoItemDTO;
import com.ap.enlatados.entity.CajaPedido;
import com.ap.enlatados.entity.Cliente;
import com.ap.enlatados.entity.Pedido;
import com.ap.enlatados.entity.Repartidor;
import com.ap.enlatados.entity.Vehiculo;
import com.ap.enlatados.service.eddlineales.Lista;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.dto.EdgeDTO;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {
	private final Lista<Pedido> lista = new Lista<>();
    

    private final CajaService cajaService;
    private final ClienteService clienteService;
    private final RepartidorService repartidorService;
    private final VehiculoService vehiculoService;

    public PedidoService(
      CajaService cajaService,
      ClienteService clienteService,
      RepartidorService repartidorService,
      VehiculoService vehiculoService
    ) {
        this.cajaService       = cajaService;
        this.clienteService    = clienteService;
        this.repartidorService = repartidorService;
        this.vehiculoService   = vehiculoService;
    }

    /**
     * Crea un pedido completo según el DTO.
     * Si es AUTOMATICO o AUTO, intenta asignar recursos; si faltan, deja Pendiente.
     */
    public Pedido crearPedido(PedidoDTO dto) {
        // 0) Validar cliente
        Cliente cliente = clienteService.buscar(dto.getDpiCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado: DPI " + dto.getDpiCliente());
        }

        // 1) Extraer cajas
        List<CajaPedido> cajasPedido = new ArrayList<>();
        for (PedidoItemDTO it : dto.getItems()) {
            List<com.ap.enlatados.entity.Caja> sacadas =
              cajaService.extraerCajas(it.getProducto(), it.getCantidad());
            if (sacadas.isEmpty()) {
                throw new IllegalArgumentException(
                  "Producto inexistente o sin stock: " + it.getProducto()
                );
            }
            if (sacadas.size() < it.getCantidad()) {
                throw new IllegalArgumentException(
                  "Stock insuficiente para producto: " + it.getProducto()
                );
            }
            for (com.ap.enlatados.entity.Caja c : sacadas) {
                cajasPedido.add(new CajaPedido(c.getId(), c.getProducto(), c.getFechaIngreso()));
            }
        }

        // 2) Asignación de repartidor y vehículo
        Repartidor rep = null;
        Vehiculo   veh = null;
        String tipo = dto.getTipoAsignacion();

        if ("MANUAL".equalsIgnoreCase(tipo)) {
            rep = repartidorService.buscar(dto.getRepartidorDpi());
            if (rep == null) {
                throw new IllegalArgumentException("No hay repartidor con DPI: " + dto.getRepartidorDpi());
            }
            repartidorService.eliminar(dto.getRepartidorDpi());

            veh = vehiculoService.buscar(dto.getVehiculoPlaca());
            if (veh == null) {
                throw new IllegalArgumentException("No hay vehículo con placa: " + dto.getVehiculoPlaca());
            }
            vehiculoService.eliminar(dto.getVehiculoPlaca());

        } else if (tipo != null && (tipo.equalsIgnoreCase("AUTOMATICO") || tipo.equalsIgnoreCase("AUTO"))) {
            // identifica ambos casos: AUTO antiguamente usado en cliente y AUTOMATICO
            rep = repartidorService.dequeue();
            veh = vehiculoService.dequeue();
            // si alguno queda null, lo tratamos como pendiente
        }

        // 3) Construir pedido
        Pedido p = new Pedido(
          dto.getDeptoOrigen(),
          dto.getDeptoDestino(),
          cliente,
          rep,
          veh
        );
        // Estado según recursos
        if (rep != null && veh != null) {
            p.setEstado("EnCurso");
        } else {
            p.setEstado("Pendiente");
        }

        // Agregar cajas y enlazar
        cajasPedido.forEach(p::agregarCaja);
        lista.add(p);
        return p;
    }


    /**
     * Completa recursos para pedidos Pendientes.
     */
    public Pedido asignarRecursosAutomatico(long id) {
        Pedido p = buscarPedido(id);
        if (p == null) throw new NoSuchElementException("Pedido no encontrado");
        if (p.getRepartidor() == null) p.setRepartidor(repartidorService.dequeue());
        if (p.getVehiculo()   == null) p.setVehiculo(vehiculoService.dequeue());
        // actualizar estado
        if (p.getRepartidor() != null && p.getVehiculo() != null) {
            p.setEstado("EnCurso");
        }
        return p;
    }

    /**
     * Completar pedido: reencola solo repartidor y vehículo (cajas consumidas).
     */
    public boolean completarPedido(long id) {
        Pedido p = buscarPedido(id);
        if (p == null) return false;
        reenqueueCompletionResources(p);
        p.setEstado("Completado");
        return true;
    }

    /**
     * Cancelar pedido: reencola cajas, repartidor y vehículo.
     */
    public boolean cancelarPedido(long id) {
        Pedido p = buscarPedido(id);
        if (p == null) return false;
        if ("EnCurso".equalsIgnoreCase(p.getEstado()) || "Pendiente".equalsIgnoreCase(p.getEstado())) {
            reenqueueCancelResources(p);
            p.setEstado("Cancelado");
            return true;
        }
        return false;
    }

    /** Listar pedidos (filtrado opcional). */
    public List<Pedido> listarPedidosPorEstado(String estado) {
        return lista.toList().stream()
            .filter(p -> estado == null
                      || estado.isEmpty()
                      || p.getEstado().equalsIgnoreCase(estado))
            .collect(Collectors.toList());
    }

    /** Eliminar solo completados o cancelados. */
    public boolean eliminarPedido(long id) {
        return lista.remove(p ->
            p.getNumeroPedido() == id &&
            ("Completado".equalsIgnoreCase(p.getEstado())
          || "Cancelado".equalsIgnoreCase(p.getEstado()))
        );
    }

    /** Buscar un pedido por número. */
    public Pedido buscarPedido(long numeroPedido) {
        return lista.find(p -> p.getNumeroPedido() == numeroPedido);
    }

    /** Diagrama de lista. */
    public DiagramDTO obtenerDiagramaPedidosDTO() {
        List<Pedido> pedidos = lista.toList();
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();

        for (int i = 0; i < pedidos.size(); i++) {
            nodes.add(new NodeDTO(i, String.valueOf(pedidos.get(i).getNumeroPedido())));
            if (i < pedidos.size() - 1) {
                edges.add(new EdgeDTO(i, i + 1));
            }
        }

        return new DiagramDTO(nodes, edges);
    }

    /** Carga masiva desde CSV. */
    public int cargarDesdeCsv(List<String[]> datos) {
        int count = 0;
        for (String[] linea : datos) {
            if (linea.length != 3) continue;
            String origen  = linea[0].trim();
            String destino = linea[1].trim();
            String dpiCli  = linea[2].trim();
            Cliente cli = clienteService.buscar(dpiCli);
            Repartidor rep = repartidorService.dequeue();
            Vehiculo veh   = vehiculoService.dequeue();
            Pedido p = new Pedido(origen, destino, cli, rep, veh);
            lista.add(p);
            count++;
        }
        return count;
    }
  

    /** Reencola solo recursos asignados (repartidor y vehículo). */
    private void reenqueueCompletionResources(Pedido p) {
        if (p.getRepartidor() != null) repartidorService.reenqueue(p.getRepartidor());
        if (p.getVehiculo()   != null) vehiculoService.reenqueue(p.getVehiculo());
    }

    /** Reencola cajas y recursos. */
    private void reenqueueCancelResources(Pedido p) {
        for (CajaPedido cp : p.getCajas()) {
            cajaService.reencolarCaja(cp.getProducto(), cp.getId(), cp.getFechaIngreso());
        }
        if (p.getRepartidor() != null) repartidorService.reenqueue(p.getRepartidor());
        if (p.getVehiculo()   != null) vehiculoService.reenqueue(p.getVehiculo());
    }

}
