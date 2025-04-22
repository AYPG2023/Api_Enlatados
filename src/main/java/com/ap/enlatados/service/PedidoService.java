package com.ap.enlatados.service;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.model.Pedido;
import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.model.Vehiculo;
import com.ap.enlatados.repository.PedidoRepository;

@Service
@Transactional
public class PedidoService {

    private static class Node {
        Pedido data; Node next;
        Node(Pedido p){ data = p; }
    }

    private Node head;
    private final PedidoRepository repo;
    private final ClienteService clienteSvc;
    private final RepartidorService repartidorSvc;
    private final VehiculoService vehiculoSvc;
    private final CajaService cajaSvc;

    public PedidoService(PedidoRepository repo,
                         ClienteService clienteSvc,
                         RepartidorService repartidorSvc,
                         VehiculoService vehiculoSvc,
                         CajaService cajaSvc) {
        this.repo = repo;
        this.clienteSvc = clienteSvc;
        this.repartidorSvc = repartidorSvc;
        this.vehiculoSvc = vehiculoSvc;
        this.cajaSvc = cajaSvc;
    }

    @PostConstruct
    private void init() {
        repo.findAll().forEach(this::insertarEnLista);
    }

    /**
     * Crea un nuevo pedido:
     * - Busca el cliente por ID (ya existe buscarPorId en ClienteService)
     * - Extrae un repartidor (FIFO)
     * - Extrae un vehículo (FIFO)
     * - Extrae numCajas cajas (LIFO)
     * - Guarda y añade a la lista enlazada interna
     */
    public Pedido crear(String origen, String destino, Long clienteId, int numCajas) {
        // 1) Cliente
        Cliente cli = Optional.ofNullable(clienteSvc.buscarPorId(clienteId))
            .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado"));

        // 2) Repartidor
        Repartidor rep = repartidorSvc.dequeue();
        if (rep == null) {
            throw new NoSuchElementException("No hay repartidores disponibles");
        }

        // 3) Vehículo
        Vehiculo veh = vehiculoSvc.dequeue();
        if (veh == null) {
            // devolvemos el repartidor a la cola
            repartidorSvc.reenqueue(rep);
            throw new NoSuchElementException("No hay vehículos disponibles");
        }

        // 4) Cajas
        for (int i = 0; i < numCajas; i++) {
            if (cajaSvc.extraer() == null) {
                // falta cajas: devolvemos recursos y error
                repartidorSvc.reenqueue(rep);
                vehiculoSvc.reenqueue(veh);
                throw new NoSuchElementException("No hay suficientes cajas disponibles");
            }
        }

        // 5) Crear Pedido
        Pedido p = new Pedido();
        p.setDeptoOrigen(origen);
        p.setDeptoDestino(destino);
        p.setFechaHoraInicio(LocalDateTime.now().toString());
        p.setEstado("Pendiente");
        p.setNumeroCajas(numCajas);
        p.setCliente(cli);
        p.setRepartidor(rep);
        p.setVehiculo(veh);

        Pedido saved = repo.save(p);
        insertarEnLista(saved);
        return saved;
    }

    /** Lista todos los pedidos **/
    public List<Pedido> listar() {
        return repo.findAll();
    }

    /** Busca un pedido por ID **/
    public Pedido buscar(Long id) {
        return repo.findById(id).orElse(null);
    }

    /** Completa el pedido: cambia estado y reencola recursos **/
    public Pedido completar(Long id) {
        Pedido p = buscar(id);
        if (p == null) {
            throw new NoSuchElementException("Pedido no encontrado");
        }
        if (!"Pendiente".equals(p.getEstado())) {
            throw new IllegalStateException("Pedido ya completado");
        }
        p.setEstado("Completado");
        repartidorSvc.reenqueue(p.getRepartidor());
        vehiculoSvc.reenqueue(p.getVehiculo());
        return repo.save(p);
    }

    /** Elimina un pedido (BD + lista enlazada) **/
    public void eliminar(Long id) {
        repo.deleteById(id);
        eliminarDeLista(id);
    }

    /** Actualiza origen y destino de un pedido **/
    public Pedido actualizar(Long id, String origen, String destino) {
        return repo.findById(id)
            .map(p -> {
                p.setDeptoOrigen(origen);
                p.setDeptoDestino(destino);
                return repo.save(p);
            })
            .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));
    }

    // --- Métodos privados para manejar la lista enlazada interna ---

    private void insertarEnLista(Pedido p) {
        Node n = new Node(p);
        if (head == null) {
            head = n;
        } else {
            Node t = head;
            while (t.next != null) {
                t = t.next;
            }
            t.next = n;
        }
    }

    private void eliminarDeLista(Long id) {
        if (head == null) return;
        if (head.data.getId().equals(id)) {
            head = head.next;
            return;
        }
        Node t = head;
        while (t.next != null) {
            if (t.next.data.getId().equals(id)) {
                t.next = t.next.next;
                return;
            }
            t = t.next;
        }
    }
}
