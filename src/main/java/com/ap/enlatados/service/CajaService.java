package com.ap.enlatados.service;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.model.Caja;
import com.ap.enlatados.repository.CajaRepository;

@Service
@Transactional
public class CajaService {

    private static class Node {
        Caja data; Node next;
        Node(Caja c){ data=c; }
    }

    private Node top;
    private final CajaRepository repo;

    public CajaService(CajaRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        // cargar todo en la pila (orden cronológico ascendente)
        List<Caja> all = repo.findAll();
        all.sort(Comparator.comparing(Caja::getId));
        all.forEach(this::pushNode);
    }

    // Push
    public Caja crear() {
        Caja c = new Caja();
        c.setFechaIngreso(LocalDateTime.now().toString());
        Caja saved = repo.save(c);
        pushNode(saved);
        return saved;
    }

    // Pop
    public Caja extraer() {
        if (top == null) return null;
        Caja c = top.data;
        top = top.next;
        repo.deleteById(c.getId());
        return c;
    }

    // Listar sin modificar la pila
    public List<Caja> listar() {
        List<Caja> out = new ArrayList<>();
        Node t = top;
        while (t != null) {
            out.add(t.data);
            t = t.next;
        }
        return out;
    }

    // Interno
    private void pushNode(Caja c) {
        Node n = new Node(c);
        n.next = top;
        top = n;
    }
}
