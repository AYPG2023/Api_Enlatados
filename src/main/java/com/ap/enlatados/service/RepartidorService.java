package com.ap.enlatados.service;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.model.Repartidor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RepartidorService {

    private static class NodoRepartidor {
        Repartidor data;
        NodoRepartidor next;
        NodoRepartidor(Repartidor r) { this.data = r; }
    }

    private NodoRepartidor front, rear;

    /** Crear o encolar repartidor */
    public void crear(Repartidor r) {
        enqueue(r);
    }

    /** Agregar a la cola */
    public void enqueue(Repartidor r) {
        NodoRepartidor node = new NodoRepartidor(r);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
    }

    /** Extraer repartidor (dequeue) */
    public Repartidor dequeue() {
        if (front == null) return null;
        Repartidor r = front.data;
        front = front.next;
        if (front == null) rear = null;
        return r;
    }

    /** Reencolar repartidor existente */
    public void reenqueue(Repartidor r) {
        enqueue(r);
    }

    /** Buscar por DPI */
    public Repartidor buscar(String dpi) {
        NodoRepartidor t = front;
        while (t != null) {
            if (t.data.getDpi().equals(dpi)) return t.data;
            t = t.next;
        }
        throw new NoSuchElementException("Repartidor no encontrado");
    }

    /** Eliminar por DPI */
    public void eliminar(String dpi) {
        List<Repartidor> temp = new ArrayList<>();
        NodoRepartidor t = front;
        while (t != null) {
            if (!t.data.getDpi().equals(dpi)) temp.add(t.data);
            t = t.next;
        }
        front = rear = null;
        temp.forEach(this::enqueue);
    }

    /** Modificar (elimina y re-crea) */
    public void modificar(String dpi, Repartidor nuevo) {
        eliminar(dpi);
        crear(nuevo);
    }

    /** Listar todos */
    public List<Repartidor> listar() {
        List<Repartidor> list = new ArrayList<>();
        NodoRepartidor t = front;
        while (t != null) {
            list.add(t.data);
            t = t.next;
        }
        return list;
    }

    /** Carga masiva desde CSV */
    public void cargarMasivo(List<String[]> datos) {
        for (String[] linea : datos) {
            if (linea.length != 6) continue;
            crear(new Repartidor(
                linea[0].trim(),
                linea[1].trim(),
                linea[2].trim(),
                linea[3].trim(),
                linea[4].trim(),
                linea[5].trim()
            ));
        }
    }

    public DiagramDTO obtenerDiagramaRepartidoresDTO() {
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();

        NodoRepartidor t = front;
        int idx = 0;
        while (t != null) {
            // nodo con etiqueta = DPI
            nodes.add(new NodeDTO(idx, t.data.getDpi()));
            // si hay siguiente, arista idx→idx+1
            if (t.next != null) {
                edges.add(new EdgeDTO(idx, idx + 1));
            }
            t = t.next;
            idx++;
        }

        return new DiagramDTO(nodes, edges);
    }
}
