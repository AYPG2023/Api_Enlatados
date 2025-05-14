package com.ap.enlatados.service;

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

    /** Crear repartidor */
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

    /** Reencolar repartidor */
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

    /** Modificar repartidor */
    public void modificar(String dpi, Repartidor nuevo) {
        eliminar(dpi);
        crear(nuevo);
    }

    /** Listar repartidores */
    public List<Repartidor> listar() {
        List<Repartidor> repartidores = new ArrayList<>();
        NodoRepartidor t = front;
        while (t != null) {
            repartidores.add(t.data);
            t = t.next;
        }
        return repartidores;
    }

    /** Cargar desde CSV */
    public void cargarMasivo(List<String[]> datos) {
        for (String[] linea : datos) {
            if (linea.length != 6) continue;
            crear(new Repartidor(
                linea[0].trim(), // dpi
                linea[1].trim(), // nombre
                linea[2].trim(), // apellidos
                linea[3].trim(), // tipoLicencia
                linea[4].trim(), // numeroLicencia
                linea[5].trim()  // telefono
            ));
        }
    }
    /** Diagrama textual de la cola */
    public String obtenerDiagramaCola() {
        StringBuilder sb = new StringBuilder();
        NodoRepartidor t = front;
        while (t != null) {
            sb.append("[").append(t.data.getDpi()).append("] -> ");
            t = t.next;
        }
        sb.append("NULL");
        return sb.toString();
    }
}
