package com.ap.enlatados.service;

import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.dto.PedidoItemDTO;
import com.ap.enlatados.model.CajaPedido;
import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.model.Pedido;
import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.model.Vehiculo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    private static class NodoPedido {
        Pedido data;
        NodoPedido next;
        NodoPedido(Pedido p) { this.data = p; }
    }
    private NodoPedido head;

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
            List<com.ap.enlatados.model.Caja> sacadas =
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
            for (com.ap.enlatados.model.Caja c : sacadas) {
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
        append(p);
        return p;
    }

    private void append(Pedido p) {
        NodoPedido nodo = new NodoPedido(p);
        if (head == null) head = nodo;
        else {
            NodoPedido cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = nodo;
        }
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
        List<Pedido> res = new ArrayList<>();
        NodoPedido cur = head;
        while (cur != null) {
            if (estado == null || estado.isEmpty() || cur.data.getEstado().equalsIgnoreCase(estado)) {
                res.add(cur.data);
            }
            cur = cur.next;
        }
        return res;
    }

    /** Eliminar solo completados o cancelados. */
    public boolean eliminarPedido(long id) {
        if (head == null) return false;
        java.util.function.Predicate<Pedido> puede = ped ->
          "Completado".equalsIgnoreCase(ped.getEstado()) || "Cancelado".equalsIgnoreCase(ped.getEstado());
        if (head.data.getNumeroPedido() == id) {
            if (!puede.test(head.data)) return false;
            head = head.next;
            return true;
        }
        NodoPedido prev = head;
        while (prev.next != null) {
            if (prev.next.data.getNumeroPedido() == id) {
                if (!puede.test(prev.next.data)) return false;
                prev.next = prev.next.next;
                return true;
            }
            prev = prev.next;
        }
        return false;
    }

    /** Buscar un pedido por número. */
    public Pedido buscarPedido(long numeroPedido) {
        NodoPedido cur = head;
        while (cur != null) {
            if (cur.data.getNumeroPedido() == numeroPedido) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    /** Diagrama de lista. */
    public String obtenerDiagramaPedidos() {
        StringBuilder sb = new StringBuilder();
        NodoPedido cur = head;
        while (cur != null) {
            sb.append("[").append(cur.data.getNumeroPedido()).append("] -> ");
            cur = cur.next;
        }
        sb.append("NULL");
        return sb.toString();
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
            append(p);
            count++;
        }
        return count;
    }
}
