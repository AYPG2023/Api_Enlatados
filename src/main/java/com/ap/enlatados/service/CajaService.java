package com.ap.enlatados.service;

import com.ap.enlatados.model.Caja;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CajaService {

    // Mapa producto → pila (LIFO) de cajas
    private final Map<String, Stack<Caja>> inventario = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * Agrega (push) 'cantidad' cajas nuevas para el producto dado.
     * @return la lista de cajas creadas.
     */
    public List<Caja> agregarCajas(String producto, int cantidad) {
        Stack<Caja> pila = inventario.computeIfAbsent(producto, k -> new Stack<>());
        List<Caja> creadas = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            Caja c = new Caja(nextId.getAndIncrement(), producto, LocalDateTime.now().toString());
            pila.push(c);
            creadas.add(c);
        }
        return creadas;
    }

    /**
     * Extrae (pop) hasta 'cantidad' cajas del tope de la pila del producto.
     * @return lista de cajas extraídas (vacía si no hay ninguna).
     */
    public List<Caja> extraerCajas(String producto, int cantidad) {
        Stack<Caja> pila = inventario.getOrDefault(producto, new Stack<>());
        List<Caja> sacadas = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad && !pila.isEmpty(); i++) {
            sacadas.add(pila.pop());
        }
        return sacadas;
    }

    /**
     * Lista todas las cajas de un producto en orden LIFO (tope primero).
     */
    public List<Caja> listarCajas(String producto) {
        Stack<Caja> pila = inventario.getOrDefault(producto, new Stack<>());
        List<Caja> copia = new ArrayList<>(pila);
        Collections.reverse(copia);  // de tope → fondo
        return copia;
    }

    /**
     * Carga masivo de cajas desde CSV, cada línea sólo lleva el ID.
     * Se presume que todas son de un mismo producto pasado por parámetro.
     * @return número de cajas cargadas.
     */
    public int cargarDesdeCsv(String producto, List<String[]> registros) {
        Stack<Caja> pila = inventario.computeIfAbsent(producto, k -> new Stack<>());
        int contador = 0;
        for (String[] linea : registros) {
            if (linea.length != 1) continue;
            long id = Long.parseLong(linea[0].trim());
            pila.push(new Caja(id, producto, LocalDateTime.now().toString()));
            nextId.updateAndGet(n -> Math.max(n, id + 1));
            contador++;
        }
        return contador;
    }

    /**
     * Genera un diagrama textual de la pila de un producto: "[idN] -> ... -> NULL"
     */
    public String obtenerDiagrama(String producto) {
        Stack<Caja> pila = inventario.getOrDefault(producto, new Stack<>());
        StringBuilder sb = new StringBuilder();
        for (int i = pila.size() - 1; i >= 0; i--) {
            sb.append("[").append(pila.get(i).getId()).append("] -> ");
        }
        sb.append("NULL");
        return sb.toString();
    }
}
