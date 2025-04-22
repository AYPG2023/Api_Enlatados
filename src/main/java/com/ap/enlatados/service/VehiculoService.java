package com.ap.enlatados.service;

import java.util.*;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.model.Vehiculo;
import com.ap.enlatados.repository.VehiculoRepository;

@Service
@Transactional
public class VehiculoService {

    private static class Node {
        Vehiculo data; Node next;
        Node(Vehiculo v){ data=v; }
    }

    private Node front, rear;
    private final VehiculoRepository repo;

    public VehiculoService(VehiculoRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        repo.findAll().forEach(this::enqueue);
    }

    // CRUD
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
            }).orElseThrow(()->new NoSuchElementException("Vehículo no encontrado"));
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
        // quitar de la cola
        List<Vehiculo> tmp = new ArrayList<>();
        while (true) {
            Vehiculo v = dequeue();
            if (v==null) break;
            if (!v.getId().equals(id)) tmp.add(v);
        }
        tmp.forEach(this::enqueue);
    }

    // FIFO ops
    public void enqueue(Vehiculo v) {
        Node node = new Node(v);
        if (rear==null) front = rear = node;
        else { rear.next = node; rear = node; }
    }

    public Vehiculo dequeue() {
        if (front==null) return null;
        Vehiculo v = front.data;
        front = front.next;
        if (front==null) rear = null;
        return v;
    }

    public void reenqueue(Vehiculo v) {
        enqueue(v);
    }
}
