package com.ap.enlatados.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.dto.LinkedVehiculoNode;
import com.ap.enlatados.model.Vehiculo;
import com.ap.enlatados.repository.VehiculoRepository;

@Service
@Transactional
public class VehiculoService {

    /**
     * Nodo interno para la cola FIFO de vehículos.
     */
    private static class VehiculoNodeInternal {
        Vehiculo data;
        VehiculoNodeInternal next;
        VehiculoNodeInternal(Vehiculo v) { this.data = v; }
    }

    private VehiculoNodeInternal front, rear;
    private final VehiculoRepository repo;

    public VehiculoService(VehiculoRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        // Carga inicial de la cola desde la BD
        repo.findAll().forEach(this::enqueue);
    }

    // ─── CRUD ──────────────────────────────────────────────────────────────

    public Vehiculo crear(Vehiculo v) {
        Vehiculo saved = repo.save(v);
        enqueue(saved);
        return saved;
    }

    public List<Vehiculo> listar() {
        return repo.findAll();
    }

    public Vehiculo buscar(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Vehiculo actualizar(Long id, Vehiculo v) {
        return repo.findById(id)
            .map(existing -> {
                existing.setPlaca(v.getPlaca());
                existing.setMarca(v.getMarca());
                existing.setModelo(v.getModelo());
                existing.setColor(v.getColor());
                existing.setAnio(v.getAnio());
                existing.setTransmision(v.getTransmision());
                return repo.save(existing);
            })
            .orElseThrow(() -> new NoSuchElementException("Vehículo no encontrado"));
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
        // Retirar de la cola
        List<Vehiculo> tmp = new ArrayList<>();
        VehiculoNodeInternal node;
        while ((node = dequeueInternal()) != null) {
            if (!node.data.getId().equals(id)) {
                tmp.add(node.data);
            }
        }
        tmp.forEach(this::enqueue);
    }

    // ─── Cola FIFO en memoria ─────────────────────────────────────────────

    public void enqueue(Vehiculo v) {
        VehiculoNodeInternal node = new VehiculoNodeInternal(v);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
    }

    /**
     * Extrae (dequeue) el vehículo del frente, sin tocar la BD.
     */
    public Vehiculo dequeue() {
        VehiculoNodeInternal n = dequeueInternal();
        return (n == null) ? null : n.data;
    }

    /** Interno: remueve el nodo del frente **/
    private VehiculoNodeInternal dequeueInternal() {
        if (front == null) return null;
        VehiculoNodeInternal n = front;
        front = front.next;
        if (front == null) rear = null;
        return n;
    }

    /** Vuelve a encolar el vehículo **/
    public void reenqueue(Vehiculo v) {
        enqueue(v);
    }

    // ─── Exponer cola como lista enlazada de DTOs ──────────────────────────

    /**
     * Devuelve la cabeza de la cola en forma de DTO recursivo.
     */
    public LinkedVehiculoNode obtenerColaEnlazada() {
        return toDto(front);
    }

    private LinkedVehiculoNode toDto(VehiculoNodeInternal n) {
        if (n == null) return null;
        LinkedVehiculoNode dto = new LinkedVehiculoNode();
        dto.id          = n.data.getId();
        dto.placa       = n.data.getPlaca();
        dto.marca       = n.data.getMarca();
        dto.modelo      = n.data.getModelo();
        dto.color       = n.data.getColor();
        dto.anio        = n.data.getAnio();
        dto.transmision = n.data.getTransmision();
        dto.next        = toDto(n.next);
        return dto;
    }
}
