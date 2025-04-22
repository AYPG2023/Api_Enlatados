package com.ap.enlatados.service;

import java.util.*;
import jakarta.annotation.PostConstruct;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.model.Repartidor;
import com.ap.enlatados.repository.RepartidorRepository;

@Service
@Transactional
public class RepartidorService {

    private static class Node {
        Repartidor data; Node next;
        Node(Repartidor r){ data=r; }
    }

    private Node front, rear;
    private final RepartidorRepository repo;

    public RepartidorService(RepartidorRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        repo.findAll().forEach(this::enqueue);
    }

    // CRUD
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
            }).orElseThrow(()->new NoSuchElementException("Repartidor no encontrado"));
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
        // quitar de la cola
        List<Repartidor> tmp = new ArrayList<>();
        while (true) {
            Repartidor r = dequeue();
            if (r==null) break;
            if (!r.getId().equals(id)) tmp.add(r);
        }
        tmp.forEach(this::enqueue);
    }

    // FIFO ops
    public void enqueue(Repartidor r) {
        Node node = new Node(r);
        if (rear==null) front = rear = node;
        else { rear.next = node; rear = node; }
    }

    public Repartidor dequeue() {
        if (front==null) return null;
        Repartidor r = front.data;
        front = front.next;
        if (front==null) rear = null;
        return r;
    }

    // Cuando un pedido termina, devuelves al repartidor:
    public void reenqueue(Repartidor r) {
        enqueue(r);
    }
}
