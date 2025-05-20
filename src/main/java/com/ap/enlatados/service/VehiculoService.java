// src/main/java/com/ap/enlatados/service/VehiculoService.java
package com.ap.enlatados.service;

import com.ap.enlatados.model.Vehiculo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehiculoService {

    private static class NodoVehiculo {
        Vehiculo data;
        NodoVehiculo next;
        NodoVehiculo(Vehiculo v) { this.data = v; }
    }

    // Cola de vehículos DISPONIBLES
    private NodoVehiculo front, rear;

    // Compatibilidad licencia → tipos de vehículo
    private static final Map<String,List<String>> LICENCIA_COMPAT = Map.of(
      "M",  List.of("MOTO"),
      "P",  List.of("CARRO"),
      "A",  List.of("CARRO","BUS_URBANO","BUS_EXTRAURBANO"),
      "C",  List.of("CARRO","REMOLQUE"),
      "CD", List.of("CARRO","REMOLQUE"),
      "CC", List.of("CARRO","REMOLQUE"),
      "MI", List.of("CARRO","MOTO"),
      "O",  List.of("CARRO","BUS_URBANO"),
      "E",  List.of("MAQUINARIA")
    );

    /**
     * Encola un vehículo disponible.
     */
    public void crear(Vehiculo v) {
        NodoVehiculo node = new NodoVehiculo(v);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
    }

    /**
     * Alias de crear(v): encola un vehículo.
     */
    public void enqueue(Vehiculo v) {
        crear(v);
    }

    /**
     * Desencola (asigna) el siguiente vehículo disponible.
     * @return el Vehiculo o null si no hay ninguno.
     */
    public Vehiculo dequeue() {
        if (front == null) return null;
        Vehiculo v = front.data;
        front = front.next;
        if (front == null) rear = null;
        return v;
    }

    /**
     * Reencola (libera) un vehículo, devolviéndolo a la cola de disponibles.
     */
    public void reenqueue(Vehiculo v) {
        enqueue(v);
    }

    /**
     * Busca un vehículo disponible por placa, sin sacarlo de la cola.
     * @throws NoSuchElementException si no lo encuentra.
     */
    public Vehiculo buscar(String placa) {
        NodoVehiculo t = front;
        while (t != null) {
            if (t.data.getPlaca().equals(placa)) return t.data;
            t = t.next;
        }
        throw new NoSuchElementException("Vehículo no encontrado o no disponible: " + placa);
    }

    /**
     * Elimina de la cola un vehículo por placa.
     * (Si ya estaba asignado, no estará aquí y lanza excepción en buscar.)
     */
    public void eliminar(String placa) {
        List<Vehiculo> temp = new ArrayList<>();
        NodoVehiculo t = front;
        while (t != null) {
            if (!t.data.getPlaca().equals(placa)) {
                temp.add(t.data);
            }
            t = t.next;
        }
        front = rear = null;
        temp.forEach(this::enqueue);
    }

    /**
     * Modifica un vehículo existente (elimínalo y créalo de nuevo con datos nuevos).
     */
    public void modificar(String placa, Vehiculo nuevo) {
        eliminar(placa);
        crear(nuevo);
    }

    /**
     * Lista todos los vehículos actualmente DISPONIBLES.
     */
    public List<Vehiculo> listar() {
        List<Vehiculo> list = new ArrayList<>();
        NodoVehiculo t = front;
        while (t != null) {
            list.add(t.data);
            t = t.next;
        }
        return list;
    }

    /**
     * Lista por tipoVehiculo.
     */
    public List<Vehiculo> listarPorTipo(String tipoVehiculo) {
        return listar().stream()
            .filter(v -> v.getTipoVehiculo().equals(tipoVehiculo))
            .collect(Collectors.toList());
    }

    /**
     * Lista los vehículos compatibles con un tipo de licencia.
     */
    public List<Vehiculo> listarPorLicencia(String tipoLicencia) {
        List<String> permitidos = LICENCIA_COMPAT.getOrDefault(tipoLicencia, Collections.emptyList());
        return listar().stream()
            .filter(v -> permitidos.contains(v.getTipoVehiculo()))
            .collect(Collectors.toList());
    }

    /**
     * Carga masiva desde CSV (cada línea con 7 campos).
     */
    public void cargarMasivo(List<String[]> datos) {
        for (String[] linea : datos) {
            if (linea.length != 7) continue;
            crear(new Vehiculo(
                linea[0].trim(),
                linea[1].trim(),
                linea[2].trim(),
                linea[3].trim(),
                Integer.parseInt(linea[4].trim()),
                linea[5].trim(),
                linea[6].trim()
            ));
        }
    }

    /**
     * Devuelve un diagrama textual de la cola: [placa] -> ... -> NULL
     */
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
