package com.ap.enlatados.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.dto.RepartidorNode;
import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.repository.RepartidorRepository;

@Service
@Transactional
public class RepartidorService {

    /**
     * Nodo interno para la cola FIFO de repartidores.
     */
    private static class RepartidorNodeInternal {
        Repartidor data;
        RepartidorNodeInternal next;
        RepartidorNodeInternal(Repartidor r) { this.data = r; }
    }

    private RepartidorNodeInternal front, rear;
    private final RepartidorRepository repo;

    public RepartidorService(RepartidorRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        // Al arrancar, cargamos todos los repartidores en la cola
        repo.findAll().forEach(this::enqueue);
    }

    // -------------------
    // Operaciones CRUD
    // -------------------

    public Repartidor crear(Repartidor r) {
        Repartidor saved = repo.save(r);
        enqueue(saved);
        return saved;
    }

    public List<Repartidor> listar() {
        return repo.findAll();
    }

    public Repartidor buscar(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Repartidor actualizar(Long id, Repartidor r) {
        return repo.findById(id)
            .map(existing -> {
                existing.setDpi(r.getDpi());
                existing.setNombre(r.getNombre());
                existing.setApellidos(r.getApellidos());
                existing.setLicencia(r.getLicencia());
                existing.setTelefono(r.getTelefono());
                return repo.save(existing);
            })
            .orElseThrow(() -> new NoSuchElementException("Repartidor no encontrado"));
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
        // Retiramos de la cola al repartidor eliminado
        List<Repartidor> temp = new ArrayList<>();
        RepartidorNodeInternal n;
        while ((n = dequeueInternal()) != null) {
            if (!n.data.getId().equals(id)) {
                temp.add(n.data);
            }
        }
        // Volvemos a encolar los que queden
        temp.forEach(this::enqueue);
    }

    // -------------------
    // FIFO nativo
    // -------------------

    public void enqueue(Repartidor r) {
        RepartidorNodeInternal node = new RepartidorNodeInternal(r);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
    }

    /**
     * Extrae (dequeue) y devuelve el repartidor del frente de la cola,
     * sin eliminarlo de la BD.
     */
    public Repartidor dequeue() {
        RepartidorNodeInternal n = dequeueInternal();
        return (n == null) ? null : n.data;
    }

    /**  
     * Operación interna que remueve el nodo del frente  
     */  
    private RepartidorNodeInternal dequeueInternal() {
        if (front == null) return null;
        RepartidorNodeInternal n = front;
        front = front.next;
        if (front == null) rear = null;
        return n;
    }

    /**
     * Vuelve a encolar un repartidor (cuando se completa un pedido).
     */
    public void reenqueue(Repartidor r) {
        enqueue(r);
    }


    // ---------------------------------------------------
    // Exponer la cola como lista enlazada de DTOs
    // ---------------------------------------------------

    /**
     * Devuelve la cabeza de la cola en forma de DTO recursivo.
     */
    public RepartidorNode obtenerColaEnlazada() {
        return toDto(front);
    }

    private RepartidorNode toDto(RepartidorNodeInternal n) {
        if (n == null) return null;
        RepartidorNode dto = new RepartidorNode();
        dto.id        = n.data.getId();
        dto.dpi       = n.data.getDpi();
        dto.nombre    = n.data.getNombre();
        dto.apellidos = n.data.getApellidos();
        dto.licencia  = n.data.getLicencia();
        dto.telefono  = n.data.getTelefono();
        dto.next      = toDto(n.next);
        return dto;
    }
}
