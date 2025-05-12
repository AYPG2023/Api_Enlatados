package com.ap.enlatados.service;

import com.ap.enlatados.model.CajaPedido;
import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.model.Pedido;
import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.model.Vehiculo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private static class NodoPedido {
        Pedido data;
        NodoPedido next;
        NodoPedido(Pedido p) { this.data = p; }
    }

    private NodoPedido head;

    /** Crear pedido */
    public Pedido crearPedido(String deptoOrigen, String deptoDestino, Cliente cliente, Repartidor repartidor, Vehiculo vehiculo) {
        Pedido p = new Pedido(deptoOrigen, deptoDestino, cliente, repartidor, vehiculo);
        NodoPedido nodo = new NodoPedido(p);
        if (head == null) {
            head = nodo;
        } else {
            NodoPedido t = head;
            while (t.next != null) t = t.next;
            t.next = nodo;
        }
        return p;
    }

    /** Agregar caja a pedido */
    public void agregarCajaAlPedido(long numeroPedido, CajaPedido c) {
        Pedido p = buscarPedido(numeroPedido);
        if (p != null) p.agregarCaja(c);
    }

    /** Buscar pedido por número */
    public Pedido buscarPedido(long numeroPedido) {
        NodoPedido t = head;
        while (t != null) {
            if (t.data.getNumeroPedido() == numeroPedido) return t.data;
            t = t.next;
        }
        return null;
    }

    /** Listar pedidos */
    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        NodoPedido t = head;
        while (t != null) {
            pedidos.add(t.data);
            t = t.next;
        }
        return pedidos;
    }

    /** Diagrama de pedidos */
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

    /** Cargar desde CSV (DeptoOrigen;DeptoDestino;DPI Cliente) */
    public void cargarDesdeCsv(List<String[]> datos, ClienteService clienteService, RepartidorService repartidorService, VehiculoService vehiculoService) {
        for (String[] linea : datos) {
            if (linea.length != 3) continue;
            Cliente cliente = clienteService.buscar(linea[2].trim());
            Repartidor repartidor = repartidorService.dequeue();
            Vehiculo vehiculo = vehiculoService.dequeue();
            crearPedido(linea[0].trim(), linea[1].trim(), cliente, repartidor, vehiculo);
        }
    }
}
