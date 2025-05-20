package com.ap.enlatados.service;

import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.dto.PedidoItemDTO;
import com.ap.enlatados.model.*;
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

    /** Crea un pedido completo según el DTO */
    public Pedido crearPedido(PedidoDTO dto) {
        // 0) Validar cliente
        Cliente cliente = clienteService.buscar(dto.getDpiCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado: DPI " + dto.getDpiCliente());
        }

        // 1) Extraer cajas (stock) y validar
        List<CajaPedido> cajasPedido = new ArrayList<>();
        for (PedidoItemDTO it : dto.getItems()) {
            List<Caja> sacadas = cajaService.extraerCajas(it.getProducto(), it.getCantidad());
            if (sacadas.isEmpty()) {
                // no existe stock alguno
                throw new IllegalArgumentException(
                  "Producto en inexistencia o falta stock para: " + it.getProducto()
                );
            }
            if (sacadas.size() < it.getCantidad()) {
                // sólo hay parte del stock pedido
                throw new IllegalArgumentException(
                  "No hay stock suficiente para el producto: " + it.getProducto()
                );
            }
            for (Caja c : sacadas) {
                cajasPedido.add(new CajaPedido(c.getId(), c.getProducto(), c.getFechaIngreso()));
            }
        }

        // 2) Asignación de repartidor y vehículo
        Repartidor rep;
        Vehiculo veh;
        if ("MANUAL".equalsIgnoreCase(dto.getTipoAsignacion())) {
            // Asignación manual: validar existencia
            rep = repartidorService.buscar(dto.getRepartidorDpi());
            if (rep == null) {
                throw new IllegalArgumentException(
                  "No hay repartidor con DPI: " + dto.getRepartidorDpi()
                );
            }
            repartidorService.eliminar(dto.getRepartidorDpi());

            veh = vehiculoService.buscar(dto.getVehiculoPlaca());
            if (veh == null) {
                throw new IllegalArgumentException(
                  "No hay vehículo con placa: " + dto.getVehiculoPlaca()
                );
            }
            vehiculoService.eliminar(dto.getVehiculoPlaca());
        } else {
            // Asignación automática: validar disponibilidad
            rep = repartidorService.dequeue();
            if (rep == null) {
                throw new IllegalArgumentException("No hay repartidor disponible para asignar");
            }
            veh = vehiculoService.dequeue();
            if (veh == null) {
                throw new IllegalArgumentException("No hay vehículo disponible para asignar");
            }
        }

        // 3) Construir pedido
        Pedido p = new Pedido(
          dto.getDeptoOrigen(),
          dto.getDeptoDestino(),
          cliente,
          rep,
          veh
        );
        cajasPedido.forEach(p::agregarCaja);
        append(p);
        return p;
    }

    private void append(Pedido p) {
        NodoPedido nodo = new NodoPedido(p);
        if (head == null) head = nodo;
        else { NodoPedido cur = head; while(cur.next!=null) cur=cur.next; cur.next=nodo; }
    }
    

    /** Asignación automática post-creación */
    public Pedido asignarRecursosAutomatico(long id) {
        Pedido p = buscarPedido(id);
        if (p == null) throw new NoSuchElementException("Pedido no encontrado");
        if (p.getRepartidor()==null) p.setRepartidor(repartidorService.dequeue());
        if (p.getVehiculo()==null)   p.setVehiculo(vehiculoService.dequeue());
        return p;
    }

    /** Completar y reencolar TODO */
    public boolean completarPedido(long id) {
        Pedido p = buscarPedido(id);
        if (p == null) return false;
        cleanupResources(p);
        p.setEstado("Completado");
        return true;
    }
    
    
    /** Listar pedidos (filtrado por estado) */
    public List<Pedido> listarPedidosPorEstado(String estado) {
        List<Pedido> res = new ArrayList<>();
        NodoPedido cur = head;
        while (cur != null) {
            if (estado == null
             || estado.isEmpty()
             || cur.data.getEstado().equalsIgnoreCase(estado)) {
                res.add(cur.data);
            }
            cur = cur.next;
        }
        return res;
    }
    
    /** 5) Cancelar pedido (reencola recursos sin borrar el pedido) */
    public boolean cancelarPedido(long id) {
        Pedido p = buscarPedido(id);
        if (p == null) return false;
        // solo si está en curso o pendiente
        if (p.getEstado().equalsIgnoreCase("EnCurso") ||
            p.getEstado().equalsIgnoreCase("Pendiente")) {
            cleanupResources(p);
            p.setEstado("Cancelado");
            return true;
        }
        return false;
    }

    /**
     * Elimina un pedido de la lista.
     * @param numeroPedido id del pedido a eliminar.
     * @return true si se eliminó, false si no existía.
     */
    /** 6) Eliminar pedido: solo completados o cancelados */
    public boolean eliminarPedido(long id) {
        if (head == null) return false;
        // helper para chequear estado
        java.util.function.Predicate<Pedido> puedeBorrar =
          ped -> ped.getEstado().equalsIgnoreCase("Completado")
               || ped.getEstado().equalsIgnoreCase("Cancelado");

        // borrar cabeza
        if (head.data.getNumeroPedido() == id) {
            if (!puedeBorrar.test(head.data)) return false;
            head = head.next;
            return true;
        }
        // borrar intermedios
        NodoPedido prev = head;
        while (prev.next != null) {
            if (prev.next.data.getNumeroPedido() == id) {
                if (!puedeBorrar.test(prev.next.data)) return false;
                prev.next = prev.next.next;
                return true;
            }
            prev = prev.next;
        }
        return false;
        
    }


    /** Buscar un pedido por su número */
    public Pedido buscarPedido(long numeroPedido) {
        NodoPedido cur = head;
        while (cur != null) {
            if (cur.data.getNumeroPedido() == numeroPedido) {
                return cur.data;
            }
            cur = cur.next;
        }
        return null;
    }

    /** Diagrama de lista */
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
    
    
    /** Reencola cajas, repartidor y vehículo */
    private void cleanupResources(Pedido p) {
        // Reencolar cajas extraídas
        for (CajaPedido cp : p.getCajas()) {
            cajaService.reencolarCaja(cp.getProducto(), cp.getId(), cp.getFechaIngreso());
        }
        // Reencolar repartidor/vehículo
        if (p.getRepartidor()!=null) repartidorService.reenqueue(p.getRepartidor());
        if (p.getVehiculo()   !=null) vehiculoService.reenqueue(p.getVehiculo());
    }

    /**
     * Carga masiva desde CSV. Retorna número de pedidos creados.
     */
    public int cargarDesdeCsv(List<String[]> datos) {
        int count = 0;
        for (String[] linea : datos) {
            if (linea.length != 3) continue;
            String origen   = linea[0].trim();
            String destino  = linea[1].trim();
            String dpiCli   = linea[2].trim();
            // creación simple: sin items ni recursos
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
