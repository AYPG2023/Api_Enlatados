package com.ap.enlatados.service;

import com.ap.enlatados.dto.AsignarPedidoDTO;
import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.dto.PedidoItemDTO;
import com.ap.enlatados.model.Caja;
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
        this.cajaService = cajaService;
        this.clienteService = clienteService;
        this.repartidorService = repartidorService;
        this.vehiculoService = vehiculoService;
    }

    /**
     * Crea un pedido, extrae las cajas del inventario y lo deja en "Pendiente".
     */
    public Pedido crearPedido(PedidoDTO dto) {
        var cliente = clienteService.buscar(dto.getDpiCliente());
        // Extraer cajas según items
        List<Caja> extraidas = new ArrayList<>();
        for (PedidoItemDTO item : dto.getItems()) {
            extraidas.addAll(
              cajaService.extraerCajas(item.getProducto(), item.getCantidad())
            );
        }
        Pedido p = new Pedido(
          dto.getDeptoOrigen(),
          dto.getDeptoDestino(),
          cliente,
          null,
          null
        );
        // Agregar cajas extraídas al pedido
        for (Caja c: extraidas) {
            p.agregarCaja(new CajaPedido(c.getId(), c.getProducto(), c.getFechaIngreso()));
        }
        append(p);
        return p;
    }

    private void append(Pedido p) {
        NodoPedido nodo = new NodoPedido(p);
        if (head == null) head = nodo;
        else {
            NodoPedido t = head;
            while (t.next != null) t = t.next;
            t.next = nodo;
        }
    }

    /**
     * Asigna manualmente repartidor y vehículo al pedido.
     */
    public Pedido asignarRecursos(long numeroPedido, AsignarPedidoDTO dto) {
        Pedido p = buscarPedido(numeroPedido);
        if (p == null) throw new NoSuchElementException("Pedido no encontrado");

        // Sacar repartidor y vehículo de las colas
        Repartidor r = repartidorService.buscar(dto.getRepartidorDpi());
        repartidorService.eliminar(dto.getRepartidorDpi());
        p.setRepartidor(r);

        Vehiculo v = vehiculoService.buscar(dto.getVehiculoPlaca());
        vehiculoService.eliminar(dto.getVehiculoPlaca());
        p.setVehiculo(v);

        return p;
    }

    /**
     * Marca como completado y reencola recursos.
     */
    public Pedido completarPedido(long numeroPedido) {
        Pedido p = buscarPedido(numeroPedido);
        if (p == null) throw new NoSuchElementException("Pedido no encontrado");
        p.setEstado("Completado");
        if (p.getRepartidor() != null) {
            repartidorService.reenqueue(p.getRepartidor());
        }
        if (p.getVehiculo() != null) {
            vehiculoService.reenqueue(p.getVehiculo());
        }
        return p;
    }

    public Pedido buscarPedido(long numeroPedido) {
        NodoPedido t = head;
        while (t != null) {
            if (t.data.getNumeroPedido() == numeroPedido) return t.data;
            t = t.next;
        }
        return null;
    }

    public List<Pedido> listarPedidos(String estado) {
        List<Pedido> res = new ArrayList<>();
        NodoPedido t = head;
        while (t != null) {
            if (estado == null || estado.isEmpty()
             || t.data.getEstado().equalsIgnoreCase(estado)) {
                res.add(t.data);
            }
            t = t.next;
        }
        return res;
    }

    public String obtenerDiagramaPedidos() {
        StringBuilder sb = new StringBuilder();
        NodoPedido t = head;
        while (t != null) {
            sb.append("[").append(t.data.getNumeroPedido()).append("] -> ");
            t = t.next;
        }
        sb.append("NULL");
        return sb.toString();
    }
   

    /**
     * Carga masiva desde CSV.
     * Cada línea debe tener: DeptoOrigen;DeptoDestino;DPICliente
     *
     * @param datos              lista de arrays con los campos de cada línea
     * @param clienteService     servicio para obtener Cliente por DPI
     * @param repartidorService  servicio de repartidores (dequeue para asignar)
     * @param vehiculoService    servicio de vehículos (dequeue para asignar)
     */
    public void cargarDesdeCsv(
        List<String[]> datos,
        com.ap.enlatados.service.ClienteService clienteService,
        RepartidorService repartidorService,
        VehiculoService vehiculoService
    ) {
        for (String[] linea : datos) {
            if (linea.length != 3) {
                // línea mal formada, saltar
                continue;
            }
            String origen   = linea[0].trim();
            String destino  = linea[1].trim();
            String dpiCli   = linea[2].trim();

            // 1) Buscar el cliente
            com.ap.enlatados.model.Cliente cli = clienteService.buscar(dpiCli);

            // 2) Asignar repartidor y vehículo (dequeue)
            Repartidor rep = repartidorService.dequeue();
            Vehiculo  veh = vehiculoService.dequeue();

            // 3) Crear el pedido en memoria
            crearPedido(origen, destino, cli, rep, veh);
        }
    }

	private void crearPedido(String origen, String destino, Cliente cli, Repartidor rep, Vehiculo veh) {
		// TODO Auto-generated method stub
		
	}

}
