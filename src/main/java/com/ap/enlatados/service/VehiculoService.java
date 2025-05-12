package com.ap.enlatados.service;

import com.ap.enlatados.model.Vehiculo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VehiculoService {

    private static class NodoVehiculo {
        Vehiculo data;
        NodoVehiculo next;
        NodoVehiculo(Vehiculo v) { this.data = v; }
    }

    private NodoVehiculo front, rear;

    /** Crear vehículo */
    public void crear(Vehiculo v) {
        enqueue(v);
    }

    /** Agregar a la cola */
    public void enqueue(Vehiculo v) {
        NodoVehiculo node = new NodoVehiculo(v);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
    }

    /** Extraer vehículo (dequeue) */
    public Vehiculo dequeue() {
        if (front == null) return null;
        Vehiculo v = front.data;
        front = front.next;
        if (front == null) rear = null;
        return v;
    }

    /** Reencolar vehículo */
    public void reenqueue(Vehiculo v) {
        enqueue(v);
    }

    /** Buscar por placa */
    public Vehiculo buscar(String placa) {
        NodoVehiculo t = front;
        while (t != null) {
            if (t.data.getPlaca().equals(placa)) return t.data;
            t = t.next;
        }
        throw new NoSuchElementException("Vehículo no encontrado");
    }

    /** Eliminar por placa */
    public void eliminar(String placa) {
        List<Vehiculo> temp = new ArrayList<>();
        NodoVehiculo t = front;
        while (t != null) {
            if (!t.data.getPlaca().equals(placa)) temp.add(t.data);
            t = t.next;
        }
        front = rear = null;
        temp.forEach(this::enqueue);
    }

    /** Modificar vehículo */
    public void modificar(String placa, Vehiculo nuevo) {
        eliminar(placa);
        crear(nuevo);
    }

    /** Listar vehículos */
    public List<Vehiculo> listar() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        NodoVehiculo t = front;
        while (t != null) {
            vehiculos.add(t.data);
            t = t.next;
        }
        return vehiculos;
    }

    /** Cargar desde CSV */
    public void cargarMasivo(List<String[]> datos) {
        for (String[] linea : datos) {
            if (linea.length != 6) continue;
            crear(new Vehiculo(
                    linea[0].trim(),
                    linea[1].trim(),
                    linea[2].trim(),
                    linea[3].trim(),
                    Integer.parseInt(linea[4].trim()),
                    linea[5].trim()
            ));
        }
    }

    /** Diagrama textual de la cola */
    public String obtenerDiagramaCola() {
        StringBuilder sb = new StringBuilder();
        NodoVehiculo t = front;
        while (t != null) {
            sb.append("[").append(t.data.getPlaca()).append("] -> ");
            t = t.next;
        }
        sb.append("NULL");
        return sb.toString();
    }
}
